package io.github.tscholze.tobbot.managers;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import io.github.tscholze.tobbot.models.BaseModel;

/**
 * Manages the communication with the Firebase Realtime Database SDK.
 */
public class FirebaseDatabaseManager
{
    /**
     * Saves a given object as child to the root.
     * @param root Name of the root node.
     * @param obj Object to create.
     */
    public static String create(String root, BaseModel obj)
    {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference(root);
        obj.id = database.push().getKey();
        database.child(obj.id).setValue(obj);

        return obj.id;
    }

    /**
     * Deletes an entry by its root and unique id.
     * @param root Name of the root node.
     * @param id Unique id of the object to delete.
     */
    public static void delete(String root, String id)
    {
        getEntry(root, id).removeValue();
    }

    /**
     * Updates an entry by its root and unique id.
     * @param root Name of the root node.
     * @param obj Object to update.
     */
    public static void update(String root, BaseModel obj)
    {
        getEntry(root, obj.id).setValue(obj);
    }

    private static DatabaseReference getEntry(String root, String id)
    {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference(root);
        return database.child(id);
    }
}
