// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access Cloud Firestore.
const admin = require('firebase-admin');
admin.initializeApp();

const db = admin.firestore()
function updateReg (change, context){
	let oldValues = change.before.data();
	oldValues = oldValues === undefined ? [] : oldValues["events_registered"];
	let newValues = change.after.data();
	newValues = newValues === undefined ? [] : newValues["events_registered"];
	let needIncrease = [];
	let needDecrease = [];
	for (let val of oldValues){
		if (newValues.indexOf(val) === -1
			&& needDecrease.indexOf(val) === -1){
			needDecrease.push(val);
		}
	}

	for (let val of newValues){
		if (oldValues.indexOf(val) === -1
			&& needIncrease.indexOf(val) === -1){
			needIncrease.push(val);
		}
	}

	for (let val of needIncrease) {
		db.collection("events").doc(val).update({
			num_reg: admin.firestore.FieldValue.increment(1)
		}).catch((e) => {});
	}

	for (let val of needDecrease) {
		db.collection("events").doc(val).update({
			num_reg: admin.firestore.FieldValue.increment(-1)
		}).catch((e) => {});
	}
}

function updateRat (change, context){
	let document = context.params.event_id;
	let oldValue = change.before.data();
	oldValue = oldValue === undefined ? undefined : change.before.data()["number"];
	let newValue = change.after.data();
	newValue = newValue === undefined ? undefined : change.after.data()["number"];
	let variation = 0;
	if (newValue === undefined && oldValue === undefined){
		return;
	}
	else if (newValue === undefined){
		variation = -1;
		newValue = 0;
	}
	else if (oldValue === undefined){
		variation = 1;
		oldValue = 0;
	}
	// eslint-disable-next-line promise/catch-or-return
	db.collection("events").doc(document).get().then(doc => {
		// eslint-disable-next-line promise/always-return
		if (doc.exists) {
			let avg = doc.data()["avg_rate"];
			let nb = doc.data()["num_rate"];
			let new_nb = nb + variation;
			let new_avg;
			if (new_nb === 0){
				new_avg = 0;
			}
			else{
				new_avg = ((avg*nb)-oldValue+newValue)/new_nb
			}
			return db.collection("events").doc(document).set({"num_rate": new_nb,"avg_rate":new_avg}, {merge: true});
		}
	}).catch((e) => {});
}
exports.updateRegistered = functions.firestore
	.document('registered/{user_id}')
	.onWrite(updateReg);

exports.updateRatings = functions.firestore
	.document('ratings/{event_id}/ratings/{user_id}')
	.onWrite(updateRat);

exports.deleteAccount = functions.auth.user().onDelete((user) => {
	db.collection("registered").doc(user.uid).delete().catch((e) => {});
});


exports.deleteEvent = functions.firestore
	.document('events/{event_id}')
	.onDelete((change, context) => {
		let doc_id = context.params.event_id;
		db.collection("ratings").doc(doc_id).delete().catch((e) => {});
		db.collection("reviews").doc(doc_id).delete().catch((e) => {});
	});

exports.isOrganiser = functions.https.onCall((data, context) => {
	const uid = context.auth.uid;
	return db.collection("organisers").where("id_user","==",uid).get().then((querySnapshot) => {
		let obj_to_send = {};
		querySnapshot.forEach((doc) => {
			obj_to_send = doc.data();
		});
		return obj_to_send;
	}).catch((error) => { return {};});
});

