package pl.allegro.devskiller.domain.assessments

class NotifierService(
    private val assessmentsNotifier: AssessmentsNotifier,
    private val candidateProvider: CandidateProvider
) {
    fun notifyAboutAssessmentsToCheck() {
        val candidatesToEvaluate = candidateProvider.getCandidatesToEvaluate()
        //todo: group by testsIds and send notification foreach group.
        val assessmentsToEvaluate = AssessmentsToEvaluate(
            candidatesToEvaluate.size,
            candidatesToEvaluate.minByOrNull { it.latestTestFinishDate!! }!!.latestTestFinishDate!!
        )
        assessmentsNotifier.notify(assessmentsToEvaluate)
    }
}
