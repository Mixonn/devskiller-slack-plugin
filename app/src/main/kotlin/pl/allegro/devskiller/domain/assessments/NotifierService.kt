package pl.allegro.devskiller.domain.assessments

import pl.allegro.devskiller.domain.assessments.notifier.AssessmentsNotifier
import pl.allegro.devskiller.domain.assessments.notifier.AssessmentsInEvaluation
import pl.allegro.devskiller.domain.assessments.provider.Assessment
import pl.allegro.devskiller.domain.assessments.provider.AssessmentsProvider

class NotifierService(
    private val assessmentsNotifier: AssessmentsNotifier,
    private val assessmentsProvider: AssessmentsProvider
) {
    fun notifyAboutAssessmentsToCheck() {
        val assessments = assessmentsProvider.getAssessmentsToEvaluate()
        if (assessments.isEmpty()) {
            return
        }
        assessmentsNotifier.notify(assessments.toStatistics())
    }

    private fun List<Assessment>.toStatistics() = AssessmentsInEvaluation(
        this.size,
        this.minByOrNull { it.finishDate }!!.finishDate
    )
}
