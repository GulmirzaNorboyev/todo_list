package uz.alif.tech.todo.common

interface OnItemClickListener<T> {
    fun onItemClicked(position: Int, item: T)
}