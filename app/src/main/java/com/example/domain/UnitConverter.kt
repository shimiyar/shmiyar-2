package com.example.domain

object UnitConverter {
    const val AVOGADRO = 6.022e23
    const val STP_MOLAR_VOLUME = 22.4 // L/mol

    data class ConversionResult(
        val moles: Double,
        val massGrams: Double,
        val gasVolumeStpLiters: Double,
        val particleCount: Double,
        val molarity: Double? = null // if volume given
    )

    fun convertFromMoles(moles: Double, molarMass: Double, solutionVolumeLiters: Double? = null): ConversionResult {
        val mass = moles * molarMass
        val volume = moles * STP_MOLAR_VOLUME
        val particles = moles * AVOGADRO
        val molarity = if (solutionVolumeLiters != null && solutionVolumeLiters > 0) moles / solutionVolumeLiters else null
        return ConversionResult(moles, mass, volume, particles, molarity)
    }

    fun convertFromMass(massGrams: Double, molarMass: Double, solutionVolumeLiters: Double? = null): ConversionResult {
        val moles = if (molarMass > 0) massGrams / molarMass else 0.0
        return convertFromMoles(moles, molarMass, solutionVolumeLiters)
    }

    fun convertFromGasVolumeStp(liters: Double, molarMass: Double, solutionVolumeLiters: Double? = null): ConversionResult {
        val moles = liters / STP_MOLAR_VOLUME
        return convertFromMoles(moles, molarMass, solutionVolumeLiters)
    }

    fun convertFromParticleCount(count: Double, molarMass: Double, solutionVolumeLiters: Double? = null): ConversionResult {
        val moles = count / AVOGADRO
        return convertFromMoles(moles, molarMass, solutionVolumeLiters)
    }

    fun convertFromMolarity(molarity: Double, solutionVolumeLiters: Double, molarMass: Double): ConversionResult {
        val moles = molarity * solutionVolumeLiters
        return convertFromMoles(moles, molarMass, solutionVolumeLiters)
    }
}
