package io.tohuwabohu.kamifusen.ssr

import kotlinx.html.*

fun MAIN.contentHeader(headingText: String) =
    header {
        classes = setOf("bg-white", "shadow")

        div {
            classes = setOf("mx-auto", "max-w-7xl", "px-4", "py-6", "sm:px-6", "lg:px-8")

            h1 {
                id = "content-heading"

                classes = setOf("text-3xl", "font-bold", "tracking-tight", "text-gray-900")

                +headingText
            }
        }
    }