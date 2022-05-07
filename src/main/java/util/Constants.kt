package util

class Constants {
    companion object {
        val numberOfSamples = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")

        // region Strings
        const val titleSoftware = "Espectro Interface"
        const val textCalibrationButtonDescription =
            "First step to obtain the concentration of ethanol in a sample. The calibration process aims to obtain the necessary parameters from samples with known ethanol concentration values. "
        const val textConcentrationButtonDescription =
            "Obtaining the concentration of ethanol in a sample from calibration. After performing the calibration process this function must be performed."
        const val titleDataRead = "Data Reading"
        const val emptyString = ""
        const val recordingText = "Recording..."
        const val textComplete = "Completed"
        const val waitingForRecording = "Waiting for recording"
        const val titleSuccessDialog = "Success"
        const val textDataRecordedDialog = "Data recorded successfully!"
        const val titleDataReferenceDialog = "Data Reference"
        const val textDataReferenceDialog = "Now you need to record the reference data (white sample)"
        const val titleDataPlotDialog = "Plot"
        const val textDataPlotDialog =
            "The recorded data will be processed and plotted.\nA short preview will be displayed and the graphics will be saved in the samples directory"
        const val titleErrorDialog = "Error"
        const val titleConcentrationDialog = "Concentration"
        const val textConcentrationDialog = "Type the concentration in the sample"
        const val titlePathDialog = "Path"
        const val textPathDialog =
            "You need to set a path to save files. \nIt's recommended that \"C:\\Documents\\EspectroInterface\" path to be setup."
        const val titleGetConcentrationDialog = "Get Concentration"
        const val textGetConcentrationDialog =
            "Has the calibration process been carried out and the files are in the same directory?"
        const val pythonCommand = "python3 "
        const val MAC_CONCENTRATION_SCRIPT = "/etoh_concentrations.py"
        const val MAC_SAMPLE_SCRIPT = "/etoh_sample.py"
        const val WIN_CONCENTRATION_SCRIPT = "\\etoh_concentrations.py"
        const val WIN_SAMPLE_SCRIPT = "\\etoh_sample.py"
        const val prefixConcentrationSample = "c"
        const val prefixRefSample = "ref"
        const val prefixConcentration = "concentrations"
        // endregion

        // region Colors
        const val darkModerateLimeGreenColor = "#388E3C"
        const val softCyanLimeGreenColor = "#69F0AE"
        // endregion

        // region Static Constants
        var scriptsPath = ""
        var isWindowsWorkstation = true
        var isMacOsWorkstation = false
        // endregion Static Constants

    }
}
