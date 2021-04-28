package net.jaggerwang.sbip.adapter.dao;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Component;
import net.jaggerwang.sbip.adapter.dao.jpa.repository.RoleRepository;
import net.jaggerwang.sbip.adapter.dao.jpa.entity.QRole;
import net.jaggerwang.sbip.adapter.dao.jpa.entity.QUser;
import net.jaggerwang.sbip.adapter.dao.jpa.entity.QUserRole;
import net.jaggerwang.sbip.adapter.dao.jpa.entity.Role;
import net.jaggerwang.sbip.entity.RoleBO;
import net.jaggerwang.sbip.usecase.port.dao.RoleDAO;

/**
 * @author Jagger Wang
 */
@Component
public class RoleDAOImpl implements RoleDAO {
    private JPAQueryFactory jpaQueryFactory;
    private RoleRepository roleJpaRepo;

    public RoleDAOImpl(JPAQueryFactory jpaQueryFactory, RoleRepository roleJpaRepo) {
        this.jpaQueryFactory = jpaQueryFactory;
        this.roleJpaRepo = roleJpaRepo;
    }

    @Override
    public RoleBO save(RoleBO roleBO) {
        return roleJpaRepo.save(Role.fromEntity(roleBO)).toEntity();
    }

    @Override
    public Optional<RoleBO> findById(Long id) {
        return roleJpaRepo.findById(id).map(role -> role.toEntity());
    }

    @Override
    public Optional<RoleBO> findByName(String name) {
        return roleJpaRepo.findByName(name).map(role -> role.toEntity());
    }

    @Override
    public List<RoleBO> rolesOfUser(Long userId) {
        var query = jpaQueryFactory.selectFrom(QRole.role)
                .join(QUserRole.userRole).on(QRole.role.id.eq(QUserRole.userRole.roleId))
                .where(QUserRole.userRole.userId.eq(userId));
        return query.fetch().stream().map(role -> role.toEntity()).collect(Collectors.toList());
    }

    @Override
    public List<RoleBO> rolesOfUser(String username) {
        var query = jpaQueryFactory.selectFrom(QRole.role)
                .join(QUserRole.userRole).on(QRole.role.id.eq(QUserRole.userRole.roleId))
                .join(QUser.user).on(QUser.user.id.eq(QUserRole.userRole.userId))
                .where(QUser.user.username.eq(username));
        return query.fetch().stream().map(role -> role.toEntity()).collect(Collectors.toList());
    }
}
