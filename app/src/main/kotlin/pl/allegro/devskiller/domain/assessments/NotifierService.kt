package pl.allegro.devskiller.domain.assessments

class NotifierService(
    private val assessmentsNotifier: AssessmentsNotifier,
    private val candidateProvider: AssessmentsProvider
) {
    fun notifyAboutAssessmentsToCheck() {
        val candidatesToEvaluate = candidateProvider.getAssessmentsToEvaluate()
        val assessmentsToEvaluate = AssessmentsToEvaluate(
            candidatesToEvaluate.size,
            candidatesToEvaluate.minByOrNull { it.finishDate!! }!!.finishDate!!
        )
        assessmentsNotifier.notify(assessmentsToEvaluate)
    }
}
