package com.app.l_pesa.profile.inter

import com.app.l_pesa.profile.model.ResUserAdditionalInfoDropdowns

interface ICallBackAdditionalInfoDropdown {
    fun onDropdownSourceOfIncomeSelected(incomeSource: ResUserAdditionalInfoDropdowns.Data.IncomeSource)
    fun onDropdownEduLvlSelected(educationalLevel: ResUserAdditionalInfoDropdowns.Data.EducationalLevel)
}