package com.example.domain.usecase.base

import com.example.domain.model.GameState
import javax.inject.Inject


class CalculateVoteResultUseCase @Inject constructor() {

    sealed class VoteOutcome {
        // 无人投票，平安日
        data object SafeDay : VoteOutcome()
        data class PlayerOut(val playerId: String) : VoteOutcome() // 有人出局
        data class TiePK(val playerIds: List<String>) : VoteOutcome() // 平票，进入PK
        data object TieNoOut : VoteOutcome() // 再次平票，无人出局
    }

    /**
     * @param votes Map<VoterId, TargetId> 投票箱
     */
    operator fun invoke(gameState: GameState, votes: Map<String, String>): VoteOutcome {
        // 1. 统计票数
        val voteCounts = votes.values.groupingBy { it }.eachCount()
        if (voteCounts.isEmpty()) return VoteOutcome.TieNoOut

        val maxVotes = voteCounts.values.maxOrNull() ?: 0

        // 2. 找到得票最高的玩家们
        val topPlayers = voteCounts.filterValues { it == maxVotes }.keys.toList()

        return when {
            // 情况 A: 只有 1 人票数最高 -> 出局
            topPlayers.size == 1 -> VoteOutcome.PlayerOut(topPlayers.first())

            // 情况 B: 多人平票
            else -> {
                if (gameState.votingRound == 0) {
                    // 第一轮平票 -> 进入 PK
                    VoteOutcome.TiePK(topPlayers)
                } else {
                    // 第二轮还平票 -> 平安日
                    VoteOutcome.TieNoOut
                }
            }
        }
    }
}