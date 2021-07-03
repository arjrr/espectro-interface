package util

class Constants {
    companion object {
        val numberOfSamples = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")

        // region Strings
        const val titleDataRead = "Data Read"
        const val emptyString = ""
        const val recordingText = "Recording..."
        const val textComplete = "Complete!"
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
        const val textConcentrationDialog = "What's the concentration in the sample?"
        const val CONCENTRATION_SCRIPT = "python3 /Users/user/Desktop/etoh_concentrations.py"
        const val prefixConcentrationSample = "c"
        const val prefixRefSample = "ref"
        const val prefixConcentration = "concentrations"
        // endregion

        // region Colors
        const val darkModerateLimeGreenColor = "#388E3C"
        const val softCyanLimeGreenColor = "#69F0AE"
        // endregion

    }
}