package com.example.notes;


import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

public class Helper
{
    static CollectionReference getCollectionReferenceForNotes() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        return FirebaseFirestore.getInstance().collection("notes").document(currentUser.getUid()).collection("my_notes");
    }

    static String timestampToString(Timestamp timestamp) {
        return new SimpleDateFormat("yyyy. MM. dd.").format(timestamp.toDate());
    }

    static List<String> colors = Arrays.asList("#000000", "#FF0000", "#E5FF00", "#00FF00", "#00C8FF", "#0000FF", "#FF00F2");
}