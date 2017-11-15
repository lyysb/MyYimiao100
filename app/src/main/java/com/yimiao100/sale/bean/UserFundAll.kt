package com.yimiao100.sale.bean

/**
 * Created by michel on 2017/8/25.
 */
data class UserFundAll (
        var id: Int,
        var userId: Int,
        var totalAmount: Double,
        var vaccineTotalAmount: Double,
        var insuranceTotalAmount: Double,
        var vaccineTotalWithdraw: Double,
        var insuranceTotalWithdraw: Double,
        var vaccineTotalSale: Double,
        var insuranceTotalSale: Double,
        var vaccineTotalExamReward: Double,
        var insuranceTotalExamReward: Double,
        var vaccineTotalAdvance: Double,
        var vaccineDeposit: Double,
        var insuranceDeposit: Double,
        var vaccineCorporateSale: Double,
        var vaccinePersonalSale: Double,
        var vaccineCorporateExamReward: Double,
        var vaccinePersonalExamReward: Double,
        var vaccineCorporateAdvance: Double,
        var vaccinePersonalAdvance: Double,
        var insuranceCorporateSale: Double,
        var insurancePersonalSale: Double,
        var insuranceCorporateExamReward: Double,
        var insurancePersonalExamReward: Double,
        var integral: Int
)