package pl.allegro.devskiller.domain.assessments

import pl.allegro.devskiller.config.assessments.ApplicationConfig
import pl.allegro.devskiller.config.assessments.TestDefinition
import pl.allegro.devskiller.domain.assessments.notifier.AssessmentsInEvaluation
import pl.allegro.devskiller.domain.assessments.notifier.AssessmentsNotifier
import pl.allegro.devskiller.domain.assessments.notifier.AssessmentsSummary
import pl.allegro.devskiller.domain.assessments.notifier.NoAssessmentsToEvaluate
import pl.allegro.devskiller.domain.assessments.provider.Assessment
import pl.allegro.devskiller.domain.assessments.provider.AssessmentsProvider

class NotifierService(
    private val assessmentsNotifier: AssessmentsNotifier,
    private val assessmentsProvider: AssessmentsProvider,
    private val applicationConfig: ApplicationConfig
) {
    fun notifyAboutAssessmentsToCheck() {
        val groupedAssessments = assessmentsProvider.getAssessmentsToEvaluate()
            .groupBy { applicationConfig.testGroups.getTestDefinition(it.testId) }
            .filter { it.key != null }
        applicationConfig.testGroups.getAllTests().forEach { (group, _) ->
            assessmentsNotifier.notify(groupedAssessments[group].toStatistics(group))
        }
    }

    private fun List<Assessment>?.toStatistics(testDefinition: TestDefinition): AssessmentsSummary =
        if (this.isNullOrEmpty()) {
            NoAssessmentsToEvaluate(testDefinition)
        } else {
            AssessmentsInEvaluation(
                testDefinition,
                this.size,
                this.minOf { it.finishDate }
            )
        }
}
