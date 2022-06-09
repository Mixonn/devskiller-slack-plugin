package pl.allegro.devskiller.domain.assessments

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
        val groupedAssessments: Map<TestGroup?, List<Assessment>> = assessmentsProvider.getAssessmentsToEvaluate()
            .groupBy { applicationConfig.testGroups.getTestDefinition(it.testId) }
            .filter { it.key != null }
        applicationConfig.testGroups.groups().forEach { group ->
            assessmentsNotifier.notify(groupedAssessments[group].toSummary(group))
        }
    }

    private fun List<Assessment>?.toSummary(testGroup: TestGroup): AssessmentsSummary =
        if (this.isNullOrEmpty()) {
            NoAssessmentsToEvaluate(testGroup)
        } else {
            AssessmentsInEvaluation(
                testGroup,
                this.size,
                this.minOf { it.finishDate }
            )
        }
}
