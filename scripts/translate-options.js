const admin = require('firebase-admin');
const { TranslationServiceClient } = require('@google-cloud/translate').v3;

admin.initializeApp({
  credential: admin.credential.cert('./serviceAccountKey.json'),
  ignoreUndefinedProperties: true,
});

const firestore = admin.firestore();

const translationClient = new TranslationServiceClient({
  keyFilename: './serviceAccountKey.json',
});

const PROJECT_ID = 'scritch-2daff'; // 🔁 replace with your actual project ID
const LOCATION = 'global'; // Or 'us-central1' if you're using that region
const TARGET_LANGUAGES = ['fr', 'es'];

const CATEGORY_DOCS = ['constraint', 'medium', 'support', 'topic'];

async function translateText(text, targetLanguage) {
  const request = {
    parent: `projects/${PROJECT_ID}/locations/${LOCATION}`,
    contents: [text],
    mimeType: 'text/plain',
    sourceLanguageCode: 'en',
    targetLanguageCode: targetLanguage,
  };

  const [response] = await translationClient.translateText(request);
  return response.translations[0].translatedText;
}

async function documentExists(collection, docId) {
  const doc = await firestore.collection(collection).doc(docId).get();
  return doc.exists;
}

async function translateDocumentData(data, targetLanguage) {
  const translated = {};

  if (typeof data.name === 'string') {
    translated.name = await translateText(data.name, targetLanguage);
  }
  if (typeof data.prompt === 'string') {
    translated.prompt = await translateText(data.prompt, targetLanguage);
  }
  if (typeof data.tips === 'string') {
    translated.tips = await translateText(data.tips, targetLanguage);
  }
  if (data.frequency !== undefined) {
    translated.frequency = data.frequency;
  }
  if (data.TipMap && typeof data.TipMap === 'object') {
    translated.TipMap = {};
    for (const [key, value] of Object.entries(data.TipMap)) {
      const newKey = await translateText(key, targetLanguage);
      const newValue = await translateText(value, targetLanguage);
      translated.TipMap[newKey] = newValue;
    }
  }

  return translated;
}

async function processOptionsCollection(categoryDoc) {
  const basePath = `categories/${categoryDoc}/options`;
  const originalDocsSnap = await firestore.collection(basePath).get();

  console.log(`📦 Found ${originalDocsSnap.size} documents in ${basePath}`);

  let translatedCount = 0;
  let skippedCount = 0;

  for (const doc of originalDocsSnap.docs) {
    const data = doc.data();
    console.log(`🔍 Processing ${doc.id}...`);

    for (const lang of TARGET_LANGUAGES) {
      try {
        const targetCollection = `categories/${categoryDoc}/options-${lang}`;
        const exists = await documentExists(targetCollection, doc.id);

        if (exists) {
          console.log(`⏭️  Skipping ${doc.id} → ${targetCollection} (already exists)`);
          skippedCount++;
        } else {
          const translatedData = await translateDocumentData(data, lang);

          await firestore
            .collection(targetCollection)
            .doc(doc.id)
            .set(translatedData);

          console.log(`✅ ${doc.id} → ${targetCollection} (newly translated)`);
          translatedCount++;
        }
      } catch (err) {
        console.error(`❌ Error processing ${doc.id} to ${lang}:`, err);
      }
    }
  }

  console.log(`🎉 Done with ${categoryDoc}: ${translatedCount} translated, ${skippedCount} skipped`);
}

async function main() {
  for (const categoryDoc of CATEGORY_DOCS) {
    try {
      await processOptionsCollection(categoryDoc);
    } catch (err) {
      console.error(`❌ Failed category ${categoryDoc}:`, err);
    }
  }
  console.log('🚀 All done!');
}

main().catch(console.error);
