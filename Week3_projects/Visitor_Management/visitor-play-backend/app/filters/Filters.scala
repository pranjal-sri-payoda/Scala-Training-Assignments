import javax.inject.Inject
import play.api.http.HttpFilters
import play.filters.cors.CORSFilter
import security.JwtAuthFilter

class Filters @Inject()(
                         corsFilter: CORSFilter,
                         jwtAuthFilter: JwtAuthFilter
                       ) extends HttpFilters {

  override val filters = Seq(
    corsFilter,   // Always apply CORS
    jwtAuthFilter // Protect /api/**
  )
}
