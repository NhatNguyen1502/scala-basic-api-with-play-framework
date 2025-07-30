package filters

import javax.inject._
import play.api.http.DefaultHttpFilters
import play.filters.cors.CORSFilter

@Singleton
class Filters @Inject() (authFilter: AuthFilter, corsFilter: CORSFilter)
    extends DefaultHttpFilters(corsFilter, authFilter)
