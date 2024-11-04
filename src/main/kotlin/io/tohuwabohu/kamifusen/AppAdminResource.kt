package io.tohuwabohu.kamifusen

import io.quarkus.logging.Log
import io.smallrye.mutiny.Uni
import io.tohuwabohu.kamifusen.crud.ApiUser
import io.tohuwabohu.kamifusen.crud.ApiUserRepository
import io.tohuwabohu.kamifusen.crud.PageRepository
import io.tohuwabohu.kamifusen.crud.dto.PageVisitDtoRepository
import io.tohuwabohu.kamifusen.ssr.*
import io.tohuwabohu.kamifusen.ssr.response.recoverWithHtmxResponse
import io.vertx.ext.web.RoutingContext
import jakarta.annotation.security.RolesAllowed
import jakarta.ws.rs.*
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.NewCookie
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.config.inject.ConfigProperty
import java.time.Instant
import java.time.LocalDateTime
import java.util.*


@Path("/")
class AppAdminResource(
    private val apiUserRepository: ApiUserRepository,
    private val pageVisitDtoRepository: PageVisitDtoRepository,
    private val pageRepository: PageRepository
) {
    @ConfigProperty(name = "quarkus.http.auth.form.cookie-name")
    lateinit var cookieName: String

    @Path("/fragment/register")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    @RolesAllowed("app-admin")
    fun registerAdmin(@FormParam("password") password: String): Uni<Response> =
        apiUserRepository.setAdminPassword(password).onItem()
            .transform { Response.ok(renderPasswordFlowSuccess()).build() }
            .onFailure().invoke { e -> Log.error("Error during admin user creation.", e) }
            .onFailure().recoverWithHtmxResponse(Response.Status.INTERNAL_SERVER_ERROR)

    @Path("/logout")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    fun logoutAdmin(@Context routingContext: RoutingContext): Uni<Response> =
        Uni.createFrom().item(
            Response.noContent().header("hx-redirect", "/")
                .cookie(
                    NewCookie.Builder(cookieName)
                    .maxAge(0)
                    .expiry(Date.from(Instant.EPOCH))
                    .path("/")
                    .build())
                .build()
        )

    @Path("/dashboard")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_HTML)
    @RolesAllowed("app-admin")
    fun renderAdminDashboard(): Uni<Response> =
        Uni.createFrom().item(Response.ok(renderAdminPage("Dashboard") {
            dashboard()
        }).build())
            .onFailure().invoke { e -> Log.error("Error during dashboard rendering.", e) }
            .onFailure().recoverWithHtmxResponse(Response.Status.INTERNAL_SERVER_ERROR)

    @Path("/stats")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_HTML)
    @RolesAllowed("app-admin")
    fun renderVisits(): Uni<Response> =
        pageVisitDtoRepository.getAllPageVisits()
            .flatMap {
                Uni.createFrom().item(Response.ok(renderAdminPage("Stats") {
                    stats(it)
                }).build())
            }
            .onFailure().invoke { e -> Log.error("Error during stats rendering.", e) }
            .onFailure().recoverWithHtmxResponse(Response.Status.INTERNAL_SERVER_ERROR)

    @Path("/pages")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_HTML)
    @RolesAllowed("app-admin")
    fun renderPageList(): Uni<Response> =
        pageRepository.listAllPages().flatMap {
            Uni.createFrom().item(Response.ok(renderAdminPage("Pages") {
                pages(it)
            }).build())
        }
            .onFailure().invoke { e -> Log.error("Error during pages rendering.", e) }
            .onFailure().recoverWithHtmxResponse(Response.Status.INTERNAL_SERVER_ERROR)

    @Path("/users")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_HTML)
    @RolesAllowed("app-admin")
    fun renderUserList(): Uni<Response> =
        apiUserRepository.listAll().flatMap {
            Uni.createFrom().item(Response.ok(renderAdminPage("Users") {
                users(it)
            }).build())
        }
            .onFailure().invoke { e -> Log.error("Error during user list rendering.", e) }
            .onFailure().recoverWithHtmxResponse(Response.Status.INTERNAL_SERVER_ERROR)

    @Path("/fragment/keygen")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowed("app-admin")
    fun renderNewApiKey(
        @FormParam("username") username: String,
        @FormParam("role") role: String,
        @FormParam("expiresAt") expiresAt: String
    ): Uni<Response> =
        apiUserRepository.addUser(
            ApiUser(
                username = username,
                role = role,
                expiresAt = when (expiresAt) {
                    "" -> null
                    else -> LocalDateTime.parse(expiresAt)
                },
            )
        ).onItem().transform { keyRaw -> Response.ok(renderCreatedApiKey(keyRaw)).build() }
            .onFailure().invoke { e -> Log.error("Error during keygen.", e) }
            .onFailure().recoverWithItem(Response.status(Response.Status.INTERNAL_SERVER_ERROR).build())

    @Path("/fragment/retire/{userId}")
    @POST
    @Produces(MediaType.TEXT_HTML)
    @RolesAllowed("app-admin")
    fun retireApiKey(userId: UUID): Uni<Response> =
        apiUserRepository.expireUser(userId).onItem()
            .transform { Response.ok().header("hx-redirect", "/users").build() }
            .onFailure().invoke { e -> Log.error("Error during key retirement", e) }
            .onFailure().recoverWithItem(Response.serverError().build())

    @Path("/fragment/pageadd")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowed("app-admin")
    fun registerNewPage(@FormParam("path") path: String): Uni<Response> =
        pageRepository.addPage(path).map { Response.ok().header("hx-redirect", "/pages").build() }
            .onFailure().invoke { e -> Log.error("Error during page registration.", e) }
            .onFailure().recoverWithItem(Response.serverError().build())

    @Path("/fragment/pagedel/{pageId}")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowed("app-admin")
    fun unregisterPage(pageId: UUID): Uni<Response> =
        pageRepository.deletePage(pageId).map { Response.ok().header("hx-redirect", "/pages").build() }
            .onFailure().invoke { e -> Log.error("Error during page registration.", e) }
            .onFailure().recoverWithItem(Response.serverError().build())
}