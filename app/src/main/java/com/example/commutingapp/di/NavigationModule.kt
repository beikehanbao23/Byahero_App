package com.example.commutingapp.di

import android.content.Context
import android.graphics.Color
import com.example.commutingapp.BuildConfig
import com.example.commutingapp.utils.others.Constants
import com.mapbox.api.directions.v5.models.Bearing
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.maps.CameraBoundsOptions
import com.mapbox.maps.plugin.locationcomponent.LocationComponentConstants
import com.mapbox.navigation.base.TimeFormat
import com.mapbox.navigation.base.extensions.applyDefaultNavigationOptions
import com.mapbox.navigation.base.extensions.applyLanguageAndVoiceUnitOptions
import com.mapbox.navigation.base.formatter.DistanceFormatterOptions
import com.mapbox.navigation.base.formatter.UnitType
import com.mapbox.navigation.base.options.NavigationOptions
import com.mapbox.navigation.ui.maps.camera.transition.NavigationCameraTransitionOptions
import com.mapbox.navigation.ui.maps.route.line.model.MapboxRouteLineOptions
import com.mapbox.navigation.ui.maps.route.line.model.RouteLineColorResources
import com.mapbox.navigation.ui.maps.route.line.model.RouteLineResources
import com.mapbox.navigation.ui.tripprogress.model.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.FragmentScoped

@Module
@InstallIn(FragmentComponent::class)
object NavigationModule {


    @Provides
    @FragmentScoped
    fun provideDistanceFormatterOptions(@ActivityContext context: Context): DistanceFormatterOptions {
        return DistanceFormatterOptions.Builder(context)
            .unitType(UnitType.METRIC)
            .build()
    }

    @Provides
    @FragmentScoped
    fun provideProgressUpdateFormatter(
        @ActivityContext context: Context,
        formatterOptions: DistanceFormatterOptions
    ): TripProgressUpdateFormatter {
        return TripProgressUpdateFormatter.Builder(context)
            .distanceRemainingFormatter(DistanceRemainingFormatter(formatterOptions))
            .timeRemainingFormatter(TimeRemainingFormatter(context))
            .percentRouteTraveledFormatter(PercentDistanceTraveledFormatter())
            .estimatedTimeToArrivalFormatter(
                EstimatedTimeToArrivalFormatter(
                    context,
                    TimeFormat.NONE_SPECIFIED
                )
            )
            .build()
    }


    @Provides
    @FragmentScoped
    fun provideCameraBoundsOptionsBuilder(): CameraBoundsOptions{
        return CameraBoundsOptions.Builder()
            .minZoom(Constants.MIN_ZOOM_LEVEL_MAPS)
            .build()
    }

    @Provides
    @FragmentScoped
    fun provideNavigationOptionsBuilder(@ActivityContext context: Context,  formatterOptions: DistanceFormatterOptions):NavigationOptions{
    return NavigationOptions.Builder(context)
            .accessToken(BuildConfig.MAPBOX_DOWNLOADS_TOKEN)
            .distanceFormatterOptions(formatterOptions)
            .build()
    }


    @Provides
    @FragmentScoped
    fun provideRouteOptionsBuilder(@ActivityContext context: Context):RouteOptions.Builder{
        return RouteOptions.builder()
            .applyDefaultNavigationOptions()
            .applyLanguageAndVoiceUnitOptions(context)
            .bearingsList(
                listOf(
                    Bearing.builder()
                        .angle(360.0)
                        .degrees(45.0)
                        .build(),
                    null
                )
            )

    }


    @Provides
    @FragmentScoped
    fun provideRouteLineColorResource():RouteLineColorResources{
        return RouteLineColorResources.Builder()
            .routeClosureColor(Color.parseColor(Constants.ROUTE_COLOR_ROAD_CLOSURE))
            .restrictedRoadColor(Color.parseColor(Constants.ROUTE_COLOR_ROAD_RESTRICTED))
            .routeHeavyCongestionColor(Color.parseColor(Constants.ROUTE_COLOR_HEAVY_CONGESTION))
            .routeSevereCongestionColor(Color.parseColor(Constants.ROUTE_COLOR_SEVERE_CONGESTION))
            .routeModerateCongestionColor(Color.parseColor(Constants.ROUTE_COLOR_MODERATE_CONGESTION))
            .routeLowCongestionColor(Color.parseColor(Constants.ROUTE_COLOR_LOW_CONGESTION))
            .build()
    }

    @Provides
    @FragmentScoped
    fun provideRouteLineResourceBuilder(routeLineColorResources: RouteLineColorResources):RouteLineResources{
        return RouteLineResources.Builder()
            .routeLineColorResources(routeLineColorResources)
            .build()
    }

    @Provides
    @FragmentScoped
    fun provideRouteLineOptionsBuilder(@ActivityContext context: Context, routeLineResources: RouteLineResources):MapboxRouteLineOptions{
        return MapboxRouteLineOptions.Builder(context)
            .displayRestrictedRoadSections(true)
            .withRouteLineBelowLayerId(LocationComponentConstants.LOCATION_INDICATOR_LAYER)
            .withRouteLineResources(routeLineResources)
            .build()
    }


    @Provides
    @FragmentScoped
    fun provideNavigationCameraTransitionOptions(): NavigationCameraTransitionOptions {
        return NavigationCameraTransitionOptions.Builder()
            .maxDuration(0)
            .build()
    }
}









