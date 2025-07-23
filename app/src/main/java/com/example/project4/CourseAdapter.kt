package com.example.project4.adapters // Create an adapters package

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.project4.R
import com.example.project4.models.Course

class CourseAdapter(private val courses: MutableList<Course>) :
    RecyclerView.Adapter<CourseAdapter.CourseViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(course: Course)
        fun onItemLongClick(course: Course, position: Int)
        fun onDeleteClick(course: Course, position: Int)
    }
    private var listener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_course, parent, false)
        return CourseViewHolder(view)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val course = courses[position]
        holder.bind(course)
    }

    override fun getItemCount(): Int = courses.size

    fun addCourse(course: Course) {
        courses.add(course)
        notifyItemInserted(courses.size - 1)
    }

    fun removeCourse(position: Int) {
        if (position >= 0 && position < courses.size) {
            courses.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun updateCourses(newCourses: List<Course>) {
        courses.clear()
        courses.addAll(newCourses)
        notifyDataSetChanged()
    }


    inner class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.textViewCourseName)
        private val instructorTextView: TextView = itemView.findViewById(R.id.textViewCourseInstructor)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.buttonDeleteItem)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener?.onItemClick(courses[position])
                }
            }
            itemView.setOnLongClickListener{
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener?.onItemLongClick(courses[position], position)
                }
                true // Consume the long click
            }
            deleteButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener?.onDeleteClick(courses[position], position)
                }
            }
        }

        fun bind(course: Course) {
            nameTextView.text = course.name
            instructorTextView.text = course.instructor
        }
    }
}