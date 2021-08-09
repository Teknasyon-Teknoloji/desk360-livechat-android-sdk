package com.desk360.livechat.data.mapper

import com.desk360.base.domain.mapper.FirebaseMapper

class OffLineMapper : FirebaseMapper<Int, Boolean>() {
    override fun map(from: Int?): Boolean {
        return from == 0
    }
}