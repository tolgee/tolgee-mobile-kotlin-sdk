package io.tolgee

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.AssetFileDescriptor
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.content.res.Resources
import android.content.res.TypedArray
import android.content.res.XmlResourceParser
import android.content.res.loader.ResourcesLoader
import android.graphics.Movie
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.TypedValue
import androidx.annotation.AnimRes
import androidx.annotation.AnimatorRes
import androidx.annotation.AnyRes
import androidx.annotation.ArrayRes
import androidx.annotation.BoolRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.Discouraged
import androidx.annotation.DrawableRes
import androidx.annotation.FontRes
import androidx.annotation.FractionRes
import androidx.annotation.IntegerRes
import androidx.annotation.LayoutRes
import androidx.annotation.PluralsRes
import androidx.annotation.RawRes
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.annotation.StyleableRes
import androidx.annotation.XmlRes
import io.tolgee.common.getQuantityStringT
import io.tolgee.common.getQuantityTextT
import io.tolgee.common.getStringArrayT
import io.tolgee.common.getStringT
import io.tolgee.common.getTextArrayT
import io.tolgee.common.getTextT
import java.io.InputStream

/**
 * Ignore Deprecation: Resources constructor is not really deprecated, apps should just not create
 * an instance like this.
 * Since we are extending the resources class, we are not affected.
 */
@Suppress("DEPRECATION")
internal class TolgeeResources(
    val base: Context,
    val tolgee: Tolgee
) : Resources(base.resources.assets, base.resources.displayMetrics, base.resources.configuration) {

    /**
     * Following methods are intercepted by Tolgee.
     */

    override fun getString(@StringRes id: Int): String {
        return base.getStringT(tolgee, id)
    }

    override fun getString(@StringRes id: Int, vararg formatArgs: Any?): String {
        return base.getStringT(tolgee, id, *formatArgs.filterNotNull().toTypedArray())
    }

    override fun getQuantityString(@PluralsRes id: Int, quantity: Int): String {
        return base.resources.getQuantityStringT(tolgee, id, quantity)
    }

    override fun getQuantityString(@PluralsRes id: Int, quantity: Int, vararg formatArgs: Any?): String {
        return base.resources.getQuantityStringT(tolgee, id, quantity, *formatArgs.filterNotNull().toTypedArray())
    }

    override fun getStringArray(@ArrayRes id: Int): Array<out String?> {
        return base.resources.getStringArrayT(tolgee, id)
    }

    override fun getText(@StringRes id: Int): CharSequence {
        return base.getTextT(tolgee, id)
    }

    override fun getQuantityText(@PluralsRes id: Int, quantity: Int): CharSequence {
        return base.resources.getQuantityTextT(tolgee, id, quantity)
    }

    override fun getTextArray(id: Int): Array<out CharSequence?> {
        return base.resources.getTextArrayT(tolgee, id)
    }

    /**
     * Following methods are proxied to preserve original behavior.
     */

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getFont(@FontRes id: Int): Typeface {
        return base.resources.getFont(id)
    }

    override fun getIntArray(@ArrayRes id: Int): IntArray {
        return base.resources.getIntArray(id)
    }

    override fun obtainTypedArray(@ArrayRes id: Int): TypedArray {
        return base.resources.obtainTypedArray(id)
    }

    override fun getDimension(@DimenRes id: Int): Float {
        return base.resources.getDimension(id)
    }

    override fun getDimensionPixelOffset(@DimenRes id: Int): Int {
        return base.resources.getDimensionPixelOffset(id)
    }

    override fun getDimensionPixelSize(@DimenRes id: Int): Int {
        return base.resources.getDimensionPixelSize(id)
    }

    override fun getFraction(@FractionRes id: Int, base: Int, pbase: Int): Float {
        return this.base.resources.getFraction(id, base, pbase)
    }

    @Deprecated("Proxied Deprecation")
    override fun getDrawable(@DrawableRes id: Int): Drawable? {
        return base.resources.getDrawable(id)
    }

    override fun getDrawable(@DrawableRes id: Int, theme: Theme?): Drawable? {
        return base.resources.getDrawable(id, theme)
    }

    @Deprecated("Proxied Deprecation")
    override fun getDrawableForDensity(@DrawableRes id: Int, density: Int): Drawable? {
        return base.resources.getDrawableForDensity(id, density)
    }

    override fun getDrawableForDensity(@DrawableRes id: Int, density: Int, theme: Theme?): Drawable? {
        return base.resources.getDrawableForDensity(id, density, theme)
    }

    @Deprecated("Proxied Deprecation")
    override fun getMovie(@RawRes id: Int): Movie? {
        return base.resources.getMovie(id)
    }

    @ColorInt
    @Deprecated("Proxied Deprecation")
    override fun getColor(@ColorRes id: Int): Int {
        return base.resources.getColor(id)
    }

    @ColorInt
    @RequiresApi(Build.VERSION_CODES.M)
    override fun getColor(@ColorRes id: Int, theme: Theme?): Int {
        return base.resources.getColor(id, theme)
    }

    @Deprecated("Proxied Deprecation")
    override fun getColorStateList(@ColorRes id: Int): ColorStateList {
        return base.resources.getColorStateList(id)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun getColorStateList(@ColorRes id: Int, theme: Theme?): ColorStateList {
        return base.resources.getColorStateList(id, theme)
    }

    override fun getBoolean(@BoolRes id: Int): Boolean {
        return base.resources.getBoolean(id)
    }

    override fun getInteger(@IntegerRes id: Int): Int {
        return base.resources.getInteger(id)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun getFloat(@DimenRes id: Int): Float {
        return base.resources.getFloat(id)
    }

    override fun getLayout(@LayoutRes id: Int): XmlResourceParser {
        return base.resources.getLayout(id)
    }

    override fun getAnimation(@AnimatorRes @AnimRes id: Int): XmlResourceParser {
        return base.resources.getAnimation(id)
    }

    override fun getXml(@XmlRes id: Int): XmlResourceParser {
        return base.resources.getXml(id)
    }

    override fun openRawResource(@RawRes id: Int): InputStream {
        return base.resources.openRawResource(id)
    }

    override fun openRawResource(@RawRes id: Int, value: TypedValue?): InputStream {
        return base.resources.openRawResource(id, value)
    }

    override fun openRawResourceFd(@RawRes id: Int): AssetFileDescriptor? {
        return base.resources.openRawResourceFd(id)
    }

    override fun getValue(@AnyRes id: Int, outValue: TypedValue?, resolveRefs: Boolean) {
        return base.resources.getValue(id, outValue, resolveRefs)
    }

    override fun getValueForDensity(
        @AnyRes id: Int,
        density: Int,
        outValue: TypedValue?,
        resolveRefs: Boolean
    ) {
        return base.resources.getValueForDensity(id, density, outValue, resolveRefs)
    }

    @SuppressLint("DiscouragedApi")
    @Discouraged("Proxied Discourage")
    override fun getValue(name: String?, outValue: TypedValue?, resolveRefs: Boolean) {
        return base.resources.getValue(name, outValue, resolveRefs)
    }

    override fun obtainAttributes(set: AttributeSet?, @StyleableRes attrs: IntArray?): TypedArray? {
        return base.resources.obtainAttributes(set, attrs)
    }

    @Deprecated("Proxied Deprecation")
    override fun updateConfiguration(config: Configuration?, metrics: DisplayMetrics?) {
        return base.resources.updateConfiguration(config, metrics)
    }

    override fun getDisplayMetrics(): DisplayMetrics? {
        return base.resources.displayMetrics
    }

    override fun getConfiguration(): Configuration? {
        return base.resources.configuration
    }

    @SuppressLint("DiscouragedApi")
    @Discouraged("Proxied Discourage")
    override fun getIdentifier(name: String?, defType: String?, defPackage: String?): Int {
        return base.resources.getIdentifier(name, defType, defPackage)
    }

    override fun getResourceName(@AnyRes resid: Int): String? {
        return base.resources.getResourceName(resid)
    }

    override fun getResourcePackageName(@AnyRes resid: Int): String? {
        return base.resources.getResourcePackageName(resid)
    }

    override fun getResourceEntryName(@AnyRes resid: Int): String? {
        return base.resources.getResourceEntryName(resid)
    }

    override fun parseBundleExtras(parser: XmlResourceParser?, outBundle: Bundle?) {
        return base.resources.parseBundleExtras(parser, outBundle)
    }

    override fun parseBundleExtra(tagName: String?, attrs: AttributeSet?, outBundle: Bundle?) {
        return base.resources.parseBundleExtra(tagName, attrs, outBundle)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun addLoaders(vararg loaders: ResourcesLoader?) {
        return base.resources.addLoaders(*loaders)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun removeLoaders(vararg loaders: ResourcesLoader?) {
        return base.resources.removeLoaders(*loaders)
    }

    override fun getResourceTypeName(@AnyRes resid: Int): String? {
        return base.resources.getResourceTypeName(resid)
    }

    override fun getText(@StringRes id: Int, def: CharSequence?): CharSequence? {
        return base.resources.getText(id, def)
    }
}