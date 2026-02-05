package com.example.domain.usecase

import com.example.domain.model.Player
import com.example.domain.model.Role
import com.example.domain.repository.WerewolfRepository

/**
 * 分配角色
 */
class AssignRoleUseCase(
    private val repository: WerewolfRepository
) {
    /**
     * @param roomId 房间ID
     * @param preferredRole 用户期望的角色 (可选，后端文档支持user_role参数)
     */
    suspend operator fun invoke(roomId: String, preferredRole: Role? = null): Result<Player> {
        if (roomId.isBlank()) {
            return Result.failure(IllegalArgumentException("房间ID不能为空"))
        }

        return repository.assignRole(roomId, preferredRole)
    }
}
