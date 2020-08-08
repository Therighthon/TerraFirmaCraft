/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.calendar;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Month;

public interface ICalendar
{
    /* Constants */
    int TICKS_IN_HOUR = 1000;
    int HOURS_IN_DAY = 24;
    int TICKS_IN_DAY = TICKS_IN_HOUR * HOURS_IN_DAY;
    int MONTHS_IN_YEAR = 12;

    /* This needs to be a float, otherwise there are ~62 minutes per hour */
    float TICKS_IN_MINUTE = TICKS_IN_HOUR / 60f;

    /* Total Calculation Methods */

    static long getTotalHours(long time)
    {
        return time / TICKS_IN_HOUR;
    }

    static long getTotalDays(long time)
    {
        return time / TICKS_IN_DAY;
    }

    static long getTotalMonths(long time, long daysInMonth)
    {
        return time / (daysInMonth * TICKS_IN_DAY);
    }

    static long getTotalYears(long time, long daysInMonth)
    {
        return 1000 + (time / (MONTHS_IN_YEAR * daysInMonth * TICKS_IN_DAY));
    }

    /* Fraction Calculation Methods */

    static int getMinuteOfHour(long time)
    {
        return (int) ((time % TICKS_IN_HOUR) / TICKS_IN_MINUTE);
    }

    static int getHourOfDay(long time)
    {
        return (int) ((time / TICKS_IN_HOUR) % HOURS_IN_DAY);
    }

    static int getDayOfMonth(long time, long daysInMonth)
    {
        return 1 + (int) ((time / TICKS_IN_DAY) % daysInMonth);
    }

    static Month getMonthOfYear(long time, long daysInMonth)
    {
        return Month.valueOf((int) ((time / (TICKS_IN_DAY * daysInMonth)) % MONTHS_IN_YEAR));
    }

    /* Format Methods */

    static ITextComponent getTimeAndDate(long time, long daysInMonth)
    {
        return ICalendar.getTimeAndDate(ICalendar.getHourOfDay(time), ICalendar.getMinuteOfHour(time), ICalendar.getMonthOfYear(time, daysInMonth), ICalendar.getDayOfMonth(time, daysInMonth), ICalendar.getTotalYears(time, daysInMonth));
    }

    static ITextComponent getTimeAndDate(int hour, int minute, Month month, int day, long years)
    {
        return new StringTextComponent(String.format("%d:%02d ", hour, minute))
            .appendSibling(new TranslationTextComponent(Helpers.getEnumTranslationKey(month)))
            .appendText(" ")
            .appendSibling(new TranslationTextComponent("tfc.tooltip.calendar_days_years", day, years));
    }

    /**
     * Gets the absolute amount of ticks passed since the world was created. This stops when no players are logged on.
     * This is safe to store timestamps.
     *
     * @return the amount of ticks since the world was created
     */
    long getTicks();

    /**
     * Gets the amount of ticks since the current date.
     * DO NOT store this in a timestamp, EVER.
     *
     * @return the amount of ticks since Jan 1, 1000
     */
    long getCalendarTicks();

    /**
     * @return the number of days in a month
     */
    int getCalendarDaysInMonth();

    /**
     * Gets the total amount of hours passed
     */
    default long getTotalHours()
    {
        return ICalendar.getTotalHours(getTicks());
    }

    /**
     * Gets the total amount of hours passed since Jan 1, 1000
     */
    default long getTotalCalendarHours()
    {
        return ICalendar.getTotalHours(getCalendarTicks());
    }

    /**
     * Gets the total amount of days passed
     */
    default long getTotalDays()
    {
        return ICalendar.getTotalDays(getTicks());
    }

    /**
     * Gets the total amount of days passed since Jan 1, 1000
     */
    default long getTotalCalendarDays()
    {
        return ICalendar.getTotalDays(getCalendarTicks());
    }

    /**
     * Gets the total amount of months passed
     */
    default long getTotalMonths()
    {
        return ICalendar.getTotalMonths(getTicks(), getCalendarDaysInMonth());
    }

    /**
     * Gets the total amount of months passed since Jan 1, 1000
     */
    default long getTotalCalendarMonths()
    {
        return ICalendar.getTotalMonths(getCalendarTicks(), getCalendarDaysInMonth());
    }

    /**
     * Gets the total amount of years passed
     */
    default long getTotalYears()
    {
        return ICalendar.getTotalYears(getTicks(), getCalendarDaysInMonth());
    }

    /**
     * Gets the total amount of years passed since Jan 1, 1000
     */
    default long getTotalCalendarYears()
    {
        return ICalendar.getTotalYears(getCalendarTicks(), getCalendarDaysInMonth());
    }

    /**
     * Get the equivalent total world time
     * World time 0 = 6:00 AM, which is calendar time 6000
     *
     * @return a value in [0, 24000) which should match the result of {@link World#getDayTime()}
     */
    default long getCalendarDayTime()
    {
        return (getCalendarTicks() - (6 * ICalendar.TICKS_IN_HOUR)) % ICalendar.TICKS_IN_DAY;
    }

    /**
     * Calculates the day of a month from the calendar time (i.e. 01 - ??)
     */
    default int getCalendarDayOfMonth()
    {
        return ICalendar.getDayOfMonth(getCalendarTicks(), getCalendarDaysInMonth());
    }

    /**
     * Gets the total number of ticks in a month.
     */
    default long getCalendarTicksInMonth()
    {
        return getCalendarDaysInMonth() * TICKS_IN_DAY;
    }

    /**
     * Gets the total number of ticks in a year.
     */
    default long getCalendarTicksInYear()
    {
        return getCalendarDaysInMonth() * MONTHS_IN_YEAR * TICKS_IN_DAY;
    }

    default ITextComponent getCalendarTimeAndDate()
    {
        return ICalendar.getTimeAndDate(getCalendarTicks(), getCalendarDaysInMonth());
    }
}
