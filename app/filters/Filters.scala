package filters

import javax.inject._
import play.api.http.DefaultHttpFilters

@Singleton
class Filters @Inject() (authFilter: AuthFilter)
    extends DefaultHttpFilters(authFilter)
