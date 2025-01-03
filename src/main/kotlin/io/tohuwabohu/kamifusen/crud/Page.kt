package io.tohuwabohu.kamifusen.crud

import io.quarkus.hibernate.reactive.panache.common.WithTransaction
import io.quarkus.hibernate.reactive.panache.kotlin.PanacheEntityBase
import io.quarkus.hibernate.reactive.panache.kotlin.PanacheRepositoryBase
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped
import jakarta.persistence.*
import org.hibernate.proxy.HibernateProxy
import java.time.LocalDateTime
import java.util.*

@Entity
@NamedQueries(
    NamedQuery(
        name = "Page.findByPathAndDomain",
        query = "FROM Page p WHERE p.path = :path AND p.domain = :domain")
)
data class Page(
    @Id
    var id: UUID,
    var path: String,
    var domain: String? = null,
    val pageAdded: LocalDateTime = LocalDateTime.now(),
    var lastHit: LocalDateTime? = null,
) : PanacheEntityBase {
    @PreUpdate
    fun updateLastHit() {
        lastHit = LocalDateTime.now()
    }

    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        val oEffectiveClass =
            if (other is HibernateProxy) other.hibernateLazyInitializer.persistentClass else other.javaClass
        val thisEffectiveClass =
            if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass else this.javaClass
        if (thisEffectiveClass != oEffectiveClass) return false
        other as Page

        return id != null && id == other.id
    }

    final override fun hashCode(): Int =
        if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass.hashCode() else javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(  id = $id   ,   path = $path   ,   domain = $domain   ,   pageAdded = $pageAdded   ,   lastHit = $lastHit )"
    }
}

@ApplicationScoped
class PageRepository : PanacheRepositoryBase<Page, UUID> {
    fun findByPageId(id: UUID) = find("id", id).firstResult()

    @WithTransaction
    fun addPageIfAbsent(path: String, domain: String): Uni<Page?> = findPageByPathAndDomain(path, domain)
        .onItem().ifNull().switchTo(addPage(path, domain))

    fun findPageByPathAndDomain(path: String, domain: String) = find("#Page.findByPathAndDomain",
        mapOf("path" to path, "domain" to domain)
    ).firstResult()

    fun listAllPages() = listAll()

    @WithTransaction
    fun addPage(path: String, domain: String): Uni<Page> {
        val page = Page(
            id = UUID.randomUUID(),
            path = path,
            domain = domain
        )

        return persist(page)
    }

    @WithTransaction
    fun deletePage(pageId: UUID): Uni<Boolean> = findById(pageId).onItem().ifNull().failWith(EntityNotFoundException()).onItem()
        .ifNotNull().transformToUni { entry -> deleteById(entry.id)}
}