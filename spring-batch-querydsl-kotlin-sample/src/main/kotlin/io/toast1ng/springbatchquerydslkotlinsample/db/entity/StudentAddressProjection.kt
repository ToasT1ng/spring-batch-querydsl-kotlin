package io.toast1ng.springbatchquerydslkotlinsample.db.entity

import com.querydsl.core.annotations.QueryProjection

class StudentAddressProjection @QueryProjection constructor(
    val studentSeqNo: Long,
    val studentAddressSeqNo: Long,
    val studentName: String,
    val studentAge: Int,
    val studentAddress: String,
) {
    override fun toString(): String {
        return "StudentAddressProjection(studentSeqNo=$studentSeqNo, studentAddressSeqNo=$studentAddressSeqNo, studentName='$studentName', studentAge=$studentAge, studentAddress='$studentAddress')"
    }
}