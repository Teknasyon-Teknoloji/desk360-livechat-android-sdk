package com.desk360.base.domain.mapper

interface IMapper<From, To> {
    fun map(from: From?): To?
}