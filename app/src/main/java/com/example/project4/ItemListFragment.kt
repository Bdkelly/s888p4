package com.example.project4

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.compose.ui.geometry.isEmpty
import androidx.compose.ui.input.key.key
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project4.adapters.CourseAdapter
import com.example.project4.models.Course
import com.google.firebase.database.* // For Realtime Database
// import com.google.firebase.firestore.* // For Firestore

class ItemsListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var courseAdapter: CourseAdapter
    private val courseList = mutableListOf<Course>()
    private lateinit var databaseReference: DatabaseReference // For Realtime DB
    // private lateinit var firestoreDb: FirebaseFirestore // For Firestore

    private val TAG = "ItemsListFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_item_list, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewItems)
        recyclerView.layoutManager = LinearLayoutManager(context)
        courseAdapter = CourseAdapter(courseList)
        recyclerView.adapter = courseAdapter

        // Initialize Firebase Database reference (Realtime Database example)
        // Replace "courses" with your desired database node name
        databaseReference = FirebaseDatabase.getInstance().getReference("courses")
        // For Firestore:
        // firestoreDb = FirebaseFirestore.getInstance()

        setupRecyclerViewItemInteractions()
        fetchCoursesFromFirebase()

        val addItemButton: Button = view.findViewById(R.id.buttonAddItem)
        addItemButton.setOnClickListener {
            // For simplicity, adding a predefined item.
            // In a real app, you'd show a dialog or another screen to input data.
            addNewCourseToFirebase(
                "New Course ${System.currentTimeMillis() % 1000}",
                "Learn something new",
                "AI Instructor"
            )
        }

        return view
    }

    private fun setupRecyclerViewItemInteractions() {
        courseAdapter.setOnItemClickListener(object : CourseAdapter.OnItemClickListener {
            override fun onItemClick(course: Course) {
                Toast.makeText(context, "Clicked: ${course.name}", Toast.LENGTH_SHORT).show()
                // Handle item click, e.g., open detail view
            }

            override fun onItemLongClick(course: Course, position: Int) {
                // Confirm deletion
                AlertDialog.Builder(requireContext())
                    .setTitle("Delete Item")
                    .setMessage("Are you sure you want to delete ${course.name}?")
                    .setPositiveButton("Delete") { _, _ ->
                        course.id?.let { deleteCourseFromFirebase(it, position) }
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        })
    }

    private fun deleteCourseFromFirebase(courseId: String, position: Int) {
        databaseReference.child(courseId).removeValue()
            .addOnSuccessListener {
                // No need to manually remove from courseList if using ValueEventListener,
                // as it will refresh the list.
                // However, if not using ValueEventListener or for immediate UI update before listener fires:
                // courseList.removeAt(position)
                // courseAdapter.notifyItemRemoved(position)
                // courseAdapter.notifyItemRangeChanged(position, courseList.size) // To update subsequent item positions

                Toast.makeText(context, "Course deleted successfully.", Toast.LENGTH_SHORT).show()
                // The ValueEventListener should update the list.
                // If you want to be absolutely sure or provide faster feedback,
                // you might remove it from the local list and notify the adapter here,
                // but be careful about concurrent modifications if the listener is also active.
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to delete course: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
                Log.e(TAG, "Error deleting course", e)
            }
    }


    private fun fetchCoursesFromFirebase() {
        // Realtime Database listener
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                courseList.clear()
                for (courseSnapshot in snapshot.children) {
                    val course = courseSnapshot.getValue(Course::class.java)
                    course?.let {
                        val courseWithId =
                            it.copy(id = courseSnapshot.key) // Store Firebase key as ID
                        courseList.add(courseWithId)
                    }
                }
                courseAdapter.notifyDataSetChanged() // Or use adapter.updateCourses for better animation
                if (courseList.isEmpty()) {
                    Toast.makeText(context, "No courses found.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Failed to read value.", error.toException())
                Toast.makeText(
                    context,
                    "Failed to load courses: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })

        // Firestore listener (example)
        /*
        firestoreDb.collection("courses")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    Toast.makeText(context, "Failed to load courses: ${e.message}", Toast.LENGTH_LONG).show()
                    return@addSnapshotListener
                }

                courseList.clear()
                for (doc in snapshots!!) {
                    val course = doc.toObject(Course::class.java).copy(id = doc.id)
                    courseList.add(course)
                }
                courseAdapter.notifyDataSetChanged()
                if (courseList.isEmpty()) {
                    Toast.makeText(context, "No courses found.", Toast.LENGTH_SHORT).show()
                }
            }
        */
    }

    private fun addNewCourseToFirebase(name: String, description: String, instructor: String) {
        // Realtime Database - push() generates a unique ID
        val courseId = databaseReference.push().key
        if (courseId == null) {
            Log.w(TAG, "Couldn't get push key for courses")
            Toast.makeText(context, "Could not create new course.", Toast.LENGTH_SHORT).show()
            return
        }
        val course =
            Course(id = courseId, name = name, description = description, instructor = instructor)
        databaseReference.child(courseId).setValue(course)
            .addOnSuccessListener {
                Toast.makeText(context, "Course added successfully.", Toast.LENGTH_SHORT).show()
                // The ValueEventListener will automatically update the list
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to add course: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
                Log.e(TAG, "Error adding course", e)
            }
    }
}
