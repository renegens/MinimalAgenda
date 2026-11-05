package domain.use_case

import domain.model.AgendaEvent
import repository.HiddenEventsRepository

class ToggleEventVisibilityUseCase(
    private val hiddenEventsRepository: HiddenEventsRepository
) {

    sealed class State {
        class Success(val agendaEvent: AgendaEvent) : State()
    }

    suspend operator fun invoke(agendaEvent: AgendaEvent): State {
        hiddenEventsRepository.toggleEventVisibility(agendaEvent)

        return State.Success(agendaEvent.copy(isHidden = !agendaEvent.isHidden))
    }
}
