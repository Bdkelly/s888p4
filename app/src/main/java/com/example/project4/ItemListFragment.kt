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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project4.adapters.CourseAdapter
import com.example.project4.models.Course
import com.google.firebase.database.*


class ItemsListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var courseAdapter: CourseAdapter
    private val courseList = mutableListOf<Course>()
    private lateinit var databaseReference: DatabaseReference

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

        databaseReference = FirebaseDatabase.getInstance().getReference("courses")

        setupRecyclerViewItemInteractions()
        fetchCoursesFromFirebase()

        val addItemButton: Button = view.findViewById(R.id.buttonAddItem)
        addItemButton.setOnClickListener {

            addNewCourseToFirebase(
                "A Good Course ${System.currentTimeMillis() % 1000}",
                "Learning something",
                "Benjamin Kelly"
            )
        }

        return view
    }

    private fun setupRecyclerViewItemInteractions() {
        courseAdapter.setOnItemClickListener(object : CourseAdapter.OnItemClickListener {
            override fun onItemClick(course: Course) {
                Toast.makeText(context, "Clicked: ${course.name}", Toast.LENGTH_SHORT).show()
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
            override fun onDeleteClick(course: Course, position: Int) {
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

                Toast.makeText(context, "Course deleted successfully.", Toast.LENGTH_SHORT).show()

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
    }

    private fun addNewCourseToFirebase(name: String, description: String, instructor: String) {
        // Realtime Database - push() generates a unique ID
        val courseId = databaseReference.push().key
        Log.w(TAG, "adding course.")
        if (courseId == null) {
            Log.w(TAG, "Couldn't get push key for courses")
            Toast.makeText(context, "Could not create new course.", Toast.LENGTH_SHORT).show()
            return
        }
        val course =
            Course(id = courseId, name = name, description = description, instructor = instructor)
        databaseReference.child(courseId).setValue(course)
            .addOnSuccessListener {
                Log.w(TAG, "adding course.")
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
