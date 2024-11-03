package io.tohuwabohu.kamifusen.ssr

import kotlinx.html.*
import kotlinx.html.classes
import kotlinx.html.stream.createHTML

fun renderAdminPage(navId: String, block: FlowContent.() -> Unit) = createHTML().html {
    attributes["lang"] = "EN"
    classes = setOf("h-full", "bg-gray-100")

    head {
        title("kamifusen - $navId")
        script(src = "/scripts/htmx.min.js") {}
        link(rel = "stylesheet", href = "/styles/main.css")
    }

    body {
        classes = setOf("h-full")

        div {
            classes = setOf("min-h-full")

            renderNavigation(navId)

            block()
        }
    }
}

private fun FlowContent.renderNavigation(navId: String) = nav {
    classes = setOf("bg-gray-800")

    div {
        classes = setOf("mx-auto", "max-w-7xl", "px-4", "sm:px-6", "lg:px-8")

        div {
            classes = setOf("flex", "h-16", "items-center", "justify-between")

            div {
                classes = setOf("flex", "items-center")

                div {
                    classes = setOf("flex-shrink-0")
                    style = "min-width: 32px;"
                    img(classes = "h-8 w-auto", src = "/static/images/kamifusen-logo.png", alt = "kamifusen logo")
                }

                div {
                    classes = setOf("hidden", "md:block")

                    div {
                        classes = setOf("ml-10", "flex", "items-baseline", "space-x-4")

                        a(href = "/dashboard") {
                            classes = setOf(
                                "rounded-md",
                                "px-3",
                                "py-2",
                                "text-sm",
                                "font-medium",
                                "text-white",
                                "hover:bg-gray-700",
                                "hover:text-white"
                            )

                            +"Dashboard"
                        }

                        a(href = "/pages") {
                            classes = setOf(
                                "rounded-md",
                                "px-3",
                                "py-2",
                                "text-sm",
                                "font-medium",
                                "text-gray-300",
                                "hover:bg-gray-700",
                                "hover:text-white"
                            )
                            +"Pages"
                        }

                        a(href = "/users") {
                            classes = setOf(
                                "rounded-md",
                                "px-3",
                                "py-2",
                                "text-sm",
                                "font-medium",
                                "text-gray-300",
                                "hover:bg-gray-700",
                                "hover:text-white"
                            )
                            +"Users"
                        }

                        a(href = "/stats") {
                            classes = setOf(
                                "rounded-md",
                                "px-3",
                                "py-2",
                                "text-sm",
                                "font-medium",
                                "text-gray-300",
                                "hover:bg-gray-700",
                                "hover:text-white"
                            )
                            +"Stats"
                        }
                    }
                }
            }

            div {
                classes = setOf("hidden", "md:block")

                div {
                    classes = setOf("ml-4", "flex", "items-center", "md:ml-6")

                    button(type = ButtonType.button) {
                        classes = setOf(
                            "relative",
                            "rounded-full",
                            "bg-gray-800",
                            "p-1",
                            "text-gray-400",
                            "hover:text-white",
                            "focus:outline-none",
                            "focus:ring-2",
                            "focus:ring-white",
                            "focus:ring-offset-2",
                            "focus:ring-offset-gray-800"
                        )

                        span {
                            classes = setOf("absolute", "-inset-1.5")
                        }
                        span {
                            classes = setOf("sr-only")
                            +"View notifications"
                        }
                    }

                    div {
                        classes = setOf("ml-10", "flex", "items-baseline", "space-x-4")
                        a(href = "#") {
                            classes = setOf(
                                "rounded-md",
                                "px-3",
                                "py-2",
                                "text-sm",
                                "font-medium",
                                "text-gray-300",
                                "hover:bg-gray-700",
                                "hover:text-white"
                            )
                            +"Logout"
                        }
                    }
                }
            }
        }
    }
}