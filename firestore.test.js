const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user: cannot read grievances", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("grievances").get());
});

test("Unauthenticated user: cannot create grievances", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(
    unauthDb.collection("grievances").doc("GP1001").set({
      userId: ALICE_UID,
      title: "Broken pipe",
      category: "Water Supply",
      description: "Water gushing out",
      status: "Pending",
      gramPanchayat: "Malur GP",
      createdAt: new Date(),
    })
  );
});

test("Authenticated user: can create their own grievance", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("grievances").doc("GP1001").set({
      userId: ALICE_UID,
      title: "Broken pipe near temple",
      category: "Water Supply",
      description: "Water is leaking heavily from main valve",
      status: "Pending",
      gramPanchayat: "Malur GP",
      isPublic: true,
      createdAt: new Date(),
    })
  );
});

test("Authenticated user: cannot create grievance with mismatched userId", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(
    aliceDb.collection("grievances").doc("GP1002").set({
      userId: BOB_UID, // Spoofed user ID
      title: "Broken pipe near temple",
      category: "Water Supply",
      description: "Water is leaking heavily from main valve",
      status: "Pending",
      gramPanchayat: "Malur GP",
      createdAt: new Date(),
    })
  );
});

test("Authenticated user: can query their own grievances", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("grievances").doc("GP1001").set({
      userId: ALICE_UID,
      title: "Low water pressure",
      category: "Water Supply",
      description: "No water in ward 4",
      status: "In Progress",
      gramPanchayat: "Malur GP",
      isPublic: false,
      createdAt: new Date(),
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("grievances").where("userId", "==", ALICE_UID).get()
  );
});

test("Authenticated user: cannot read another user's private grievance", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("grievances").doc("GP_BOB_PRIVATE").set({
      userId: BOB_UID,
      title: "Private family issue",
      category: "Other Civic Issues",
      description: "Private details",
      status: "Pending",
      gramPanchayat: "Malur GP",
      isPublic: false,
      createdAt: new Date(),
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("grievances").doc("GP_BOB_PRIVATE").get());
});

test("Authenticated user: can read another user's public grievance", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("grievances").doc("GP_BOB_PUBLIC").set({
      userId: BOB_UID,
      title: "Broken road",
      category: "Roads & Streetlights",
      description: "Pothole on main road",
      status: "Pending",
      gramPanchayat: "Malur GP",
      isPublic: true,
      createdAt: new Date(),
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(aliceDb.collection("grievances").doc("GP_BOB_PUBLIC").get());
});

test("Authenticated user: can update rating on their own grievance", async () => {
  const createdDate = new Date();
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("grievances").doc("GP1001").set({
      userId: ALICE_UID,
      title: "Low water pressure",
      category: "Water Supply",
      description: "No water in ward 4",
      status: "Resolved",
      gramPanchayat: "Malur GP",
      isPublic: true,
      createdAt: createdDate,
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("grievances").doc("GP1001").update({
      rating: 5,
      feedbackComment: "Excellent work by panchayat",
      updatedAt: new Date(),
    })
  );
});
