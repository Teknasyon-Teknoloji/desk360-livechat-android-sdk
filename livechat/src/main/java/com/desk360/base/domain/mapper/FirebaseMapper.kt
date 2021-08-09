package com.desk360.base.domain.mapper

import com.google.firebase.database.DataSnapshot
import java.lang.reflect.ParameterizedType

abstract class FirebaseMapper<Entity, Model> : IMapper<Entity, Model> {
    fun map(dataSnapshot: DataSnapshot?): Model? {
        val entity = dataSnapshot?.getValue(entityClass)
        return map(entity)
    }

    fun mapList(dataSnapshot: DataSnapshot): List<Model> {
        val list: MutableList<Model> = ArrayList()
        for (item in dataSnapshot.children) {
            map(item)?.let { list.add(it) }
        }
        return list
    }

    private val entityClass: Class<Entity>
        private get() {
            val superclass: ParameterizedType = javaClass.genericSuperclass as ParameterizedType
            return superclass.actualTypeArguments[0] as Class<Entity>
        }
}