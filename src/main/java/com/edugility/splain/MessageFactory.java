/* -*- mode: Java; c-basic-offset: 2; indent-tabs-mode: nil; coding: utf-8-unix -*-
 *
 * Copyright (c) 2013 Edugility LLC.
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense and/or sell copies
 * of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT.  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *
 * The original copy of this license is available at
 * http://www.opensource.org/license/mit-license.html.
 */
package com.edugility.splain;

import java.io.Serializable;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.LinkedHashSet;

import com.edugility.objexj.Pattern;
import com.edugility.objexj.Matcher;

import org.mvel2.templates.TemplateRuntime;

/**
 * A factory for localized messages appropriate for object graphs.
 *
 * <p>To use a {@link MessageFactory}, {@linkplain #MessageFactory()
 * create a new instance} and {@linkplain
 * #addPatterns(ResourceBundleKey, Iterable) add <code>Pattern</code>s
 * to it}.  Then pass a {@link List} of items to the {@link
 * #getMessage(List, Locale)} method.</p>
 *
 * <p>This class is not safe for concurrent use by multiple {@link
 * Thread}s.</p>
 *
 * @param <T> the type of {@link Object} used by the {@link
 * #getMessage(List, Locale)} method; the type of {@link Object} used by the
 * {@link Pattern}s that help select messages
 *
 * @author <a href="http://about.me/lairdnelson"
 * target="_parent">Laird Nelson</a>
 *
 * @see #getMessage(List, Locale)
 */
public class MessageFactory<T> implements Serializable {

  /**
   * The version of this class for {@linkplain Serializable
   * serialization purposes}.
   */
  private static final long serialVersionUID = 1L;

  /**
   * A {@link Map} of {@link Set}s of {@link Pattern}s, indexed by
   * {@link ResourceBundleKey}s.
   *
   * <p>This field may be {@code null}.</p>
   */
  private Map<ResourceBundleKey, Set<Pattern<T>>> patterns;

  /**
   * Creates a new {@link MessageFactory}.
   */
  public MessageFactory() {
    super();
  }

  /**
   * Adds a {@link Pattern} to the {@link Set} of {@link Pattern}s
   * indexed under the supplied {@link ResourceBundleKey} and returns
   * the full {@link Set} of such {@link Pattern}s.  If no such {@link
   * Set} exists, one is created whose sole contents are the supplied
   * {@link Pattern}.
   *
   * <p>This method never returns {@code null}.</p>
   *
   * <p>{@link Pattern}s may also be added by {@linkplain
   * #getPatterns(ResourceBundleKey) retrieving a <code>Set</code> of
   * <code>Pattern</code>s} and {@linkplain Set#add(Object) adding} a
   * {@link Pattern} to it directly.</p>
   *
   * @param key the {@link ResourceBundleKey} under which the supplied
   * {@link Pattern} is to be indexed; must not be {@code null}
   *
   * @param pattern the {@link Pattern} to add; must not be {@code
   * null}
   *
   * @return the full {@link Set} of {@link Pattern}s indexed under
   * the supplied {@code key}; never {@code null}
   *
   * @exception IllegalArgumentException if {@code key} or {@code
   * pattern} is {@code null}
   *
   * @see Pattern
   *
   * @see ResourceBundleKey
   *
   * @see #addPatterns(ResourceBundleKey, Iterable)
   *
   * @see #getPatterns(ResourceBundleKey)
   */
  public final Set<Pattern<T>> addPattern(final ResourceBundleKey key, final Pattern<T> pattern) {
    if (key == null) {
      throw new IllegalArgumentException("key", new NullPointerException("key"));
    }
    if (pattern == null) {
      throw new IllegalArgumentException("pattern", new NullPointerException("patterns"));
    }
    return this.addPatterns(key, Collections.singleton(pattern));
  }

  /**
   * Adds the supplied {@link Iterable} of {@link Pattern}s to the
   * {@link Set} of {@link Pattern}s indexed under the supplied {@code
   * key} and returns the full {@link Set} of such {@link Pattern}s
   * that results from this addition.
   *
   * <p>{@link Pattern}s may also be added by {@linkplain
   * #getPatterns(ResourceBundleKey) retrieving a <code>Set</code> of
   * <code>Pattern</code>s} and {@linkplain Set#add(Object) adding} a
   * {@link Pattern} to it directly.</p>
   *
   * <p>This method never returns {@code null}.</p>
   *
   * @param key the {@link ResourceBundleKey} under which the {@link
   * Pattern} is to be stored; must not be {@code null}
   *
   * @param patterns an {@link Iterable} of {@link Pattern}s to add;
   * no references are kept to this object; must not be {@code null}
   *
   * @return the full {@link Set} of {@link Pattern}s indexed under
   * the supplied {@code key}; never {@code null}
   *
   * @exception IllegalArgumentException if either parameter is {@code
   * null}
   *
   * @see Pattern
   *
   * @see ResourceBundleKey
   *
   * @see #getPatterns(ResourceBundleKey)
   */
  public Set<Pattern<T>> addPatterns(final ResourceBundleKey key, final Iterable<? extends Pattern<T>> patterns) {
    if (key == null) {
      throw new IllegalArgumentException("key", new NullPointerException("key"));
    }
    if (patterns == null) {
      throw new IllegalArgumentException("patterns", new NullPointerException("patterns"));
    }
    if (this.patterns == null) {
      this.patterns = new LinkedHashMap<ResourceBundleKey, Set<Pattern<T>>>();
    }
    Set<Pattern<T>> patternSet = this.patterns.get(key);
    if (patternSet == null) {
      patternSet = new LinkedHashSet<Pattern<T>>();
      this.patterns.put(key, patternSet);
    }
    for (final Pattern<T> pattern : patterns) {
      if (pattern != null) {
        patternSet.add(pattern);
      }
    }
    return patternSet;
  }

  /**
   * Returns a {@link Set} of {@link Pattern}s indexed under the
   * supplied {@code key}, or {@code null} if there is no such {@link
   * Set}.
   *
   * <p>The {@link Set} that is returned is the actual {@link Set}
   * used internally by this {@link MessageFactory} and is
   * mutable.</p>
   *
   * <p>This method may return {@code null}.</p>
   *
   * @param key the {@link ResourceBundleKey} whose {@link Pattern}s
   * are to be retrieved; must not be {@code null}
   *
   * @return a {@link Set} of {@link Pattern}s indexed under the
   * supplied {@code key}, or {@code null}
   *
   * @exception IllegalArgumentException if {@code key} is {@code
   * null}
   */
  public Set<Pattern<T>> getPatterns(final ResourceBundleKey key) {
    if (key == null) {
      throw new IllegalArgumentException("key", new NullPointerException("key"));
    }
    final Set<Pattern<T>> returnValue;
    if (this.patterns != null && !this.patterns.isEmpty()) {
      returnValue = this.patterns.get(key);
    } else {
      returnValue = null;
    }
    return returnValue;
  }

  /**
   * Removes the {@link Set} of {@link Pattern}s indexed under the
   * supplied {@code key} and returns it.
   *
   * <p>This method may return {@code null}.</p>
   *
   * @param key a {@link ResourceBundleKey} whose associated {@link
   * Pattern}s are to be removed; must not be {@code null}
   *
   * @return a {@link Set} of {@link Pattern}s that was removed, or
   * {@code null}
   *
   * @exception IllegalArgumentException if {@code key} was {@code
   * null}
   */
  public Set<Pattern<T>> removePatterns(final ResourceBundleKey key) {
    if (key == null) {
      throw new IllegalArgumentException("key", new NullPointerException("key"));
    }
    final Set<Pattern<T>> returnValue;
    if (this.patterns == null || this.patterns.isEmpty()) {
      returnValue = null;
    } else {
      returnValue = this.patterns.remove(key);
    }
    return returnValue;
  }

  /**
   * Formats or transforms the supplied {@code rawMessage} {@link
   * Object} in some way, perhaps by using the information stored as
   * part of the supplied {@link Matcher}, and returns the formatted
   * or transformed {@link Object}.
   *
   * <p>This implementation checks to see if the supplied {@code
   * rawMessage} is an instance of {@link CharSequence}.  If so, it is
   * treated as an <a href="http://mvel.codehaus.org/">MVEL</a> <a
   * href="http://mvel.codehaus.org/Templating+Guide">template</a>.
   * The template is interpolated using all the {@linkplain
   * Matcher#group(int) capture groups} and {@linkplain
   * Matcher#getVariables() variables} that the supplied {@link
   * Matcher} is capable of providing.</p>
   *
   * <p>Specifically, <a
   * href="http://mvel.codehaus.org/MVEL+2.0+Orb+Tags">orb tags</a>
   * may have bodies that reference the {@linkplain
   * Matcher#getVariables() variables contained by the supplied
   * <code>Matcher</code>}, as well as {@linkplain Matcher#group(int) its
   * capture groups}.  Capture group variable references begin with
   * the {@code $} character, so the following orb tag would return a
   * {@link List} representing the full match:</p>
   *
   * <blockquote><code>@{$0}</code></blockquote>
   *
   * @param rawMessage the unformatted message as returned by the
   * {@link #getMessage(List, Locale)} method; may be {@code null}
   *
   * @param matcher the {@link Matcher} that was used by the {@link
   * #getMessage(List, Locale)} method in producing the {@code
   * rawMessage} parameter value; may be {@code null}
   *
   * @return a formatted version of the supplied {@code rawMessage},
   * which may simply be the supplied {@code rawMessage} if no formatting
   * could be performed
   *
   * @see TemplateRuntime
   *
   * @see #getMessage(List, Locale)
   */
  protected Object format(final Object rawMessage, final Matcher<T> matcher) {
    final Object returnValue;
    if (rawMessage instanceof CharSequence) {
      final String template = rawMessage.toString();
      if (template != null && matcher != null) {
        final Map<?, ?> matcherVariables = matcher.getVariables();
        final int matcherVariablesSize = matcherVariables == null || matcherVariables.isEmpty() ? 0 : matcherVariables.size();
        final int groupCount = matcher.groupCount();
        final Map<Object, Object> variables = new HashMap<Object, Object>(matcherVariablesSize + groupCount);
        for (int i = 0; i < groupCount; i++) {
          variables.put(String.format("$%d", Integer.valueOf(i)), matcher.group(i));
        }
        if (matcherVariablesSize > 0) {
          variables.putAll(matcherVariables);
        }
        returnValue = TemplateRuntime.eval(template, variables);
      } else {
        returnValue = rawMessage;
      }
    } else {
      returnValue = null;
    }
    return returnValue;
  }

  /**
   * Given a {@link List} of {@link Object}s of type {@link
   * MessageFactory T}, matches that {@link List} against all the
   * {@link Pattern}s that have been {@linkplain
   * #addPattern(ResourceBundleKey, Pattern) added} to this {@link
   * MessageFactory} in insertion order, and, when one of them
   * {@linkplain Matcher#lookingAt() has a match}, selects and
   * {@linkplain #format(Object, Matcher) formats} the associated key,
   * {@linkplain #convert(Object) converts it to a
   * <code>String</code>} and returns the result.
   *
   * <p>This method may return {@code null}.</p>
   *
   * @param input the {@link List} of {@link Object}s of type {@link
   * MessageFactory T} to match; may be {@code null}
   *
   * @return a {@linkplain #convert(Object) converted} and {@linkplain
   * #format(Object, Matcher) formatted} message, or {@code null}
   *
   * @see #convert(Object)
   *
   * @see #format(Object, Matcher)
   */
  public String getMessage(final List<? extends T> input, final Locale locale) {
    final String returnValue;
    final Selector<T> selector = this.getSelector(input);
    if (selector == null) {
      returnValue = null;
    } else {
      final ResourceBundleKey key = selector.getKey();
      if (key == null) {
        returnValue = this.convert(this.format(null, selector.getMatcher()));
      } else {
        returnValue = this.convert(this.format(key.getObject(locale), selector.getMatcher()));
      }
    }
    return returnValue;
  }

  /**
   * Converts the supplied {@link Object} into a {@link String}.  The
   * default implementation of this method returns {@code null} if the
   * supplied {@link Object} is {@code null}, or the result of
   * invoking {@link Object#toString()} on it if it is not.
   *
   * <p>This method may return {@code null}.</p>
   *
   * @param object the {@link Object} to convert; may be {@code null}
   *
   * @return a {@link String} that is the result of converting the
   * supplied {@link Object}, or {@code null}
   */
  protected String convert(final Object object) {
    final String returnValue;
    if (object == null) {
      returnValue = null;
    } else {
      returnValue = object.toString();
    }
    return returnValue;
  }

  /**
   * Calls {@link #getMessage(List, Locale)}, and if the return value
   * is {@code null}, returns the supplied {@code defaultValue}
   * parameter as the return value instead.
   *
   * <p>This method may return {@code null}.</p>
   *
   * <p>This method suppresses any {@link MissingResourceException}s
   * thrown by {@link #getMessage(List, Locale)} and returns the
   * supplied {@code defaultValue} parameter in such cases.</p>
   *
   * @return a formatted message, or the {@code defaultValue}
   * parameter, or {@code null} if the {@code defaultValue} parameter
   * is {@code null} itself
   */
  public String getMessage(final List<? extends T> input, final Locale locale, final String defaultValue) {
    String returnValue = null;
    try {
      returnValue = this.getMessage(input, locale);
    } catch (final MissingResourceException oops) {
      // TODO: log
      returnValue = null;
    }
    if (returnValue == null) {
      returnValue = defaultValue;
    }
    return returnValue;
  }

  /**
   * Returns a {@link Selector} for the supplied {@link List} of
   * {@link Object}s of type {@link MessageFactory T}.
   *
   * <p>This method may return {@code null}.</p>
   *
   * @param input the {@link List} of {@link Object}s of type {@link
   * MessageFactory T} for which a {@link Selector} will be returned;
   * may be {@code null}; will be passed to the {@link
   * Pattern#matcher(List)} method
   *
   * @return a {@link Selector}, or {@code null}
   */
  final Selector<T> getSelector(final List<? extends T> input) {
    Selector<T> returnValue = null;
    if (this.patterns != null && !this.patterns.isEmpty()) {
      final Set<Entry<ResourceBundleKey, Set<Pattern<T>>>> entrySet = this.patterns.entrySet();
      if (entrySet != null && !entrySet.isEmpty()) {
        ENTRY_SET_LOOP:
        for (final Entry<ResourceBundleKey, Set<Pattern<T>>> entry : entrySet) {
          if (entry != null) {
            final ResourceBundleKey key = entry.getKey();
            if (key != null) {
              final Set<Pattern<T>> patterns = entry.getValue();
              if (patterns != null && !patterns.isEmpty()) {
                for (final Pattern<T> pattern : patterns) {
                  if (pattern != null) {
                    final Matcher<T> matcher = pattern.matcher(input);
                    assert matcher != null;
                    if (matcher.lookingAt()) {
                      returnValue = new Selector<T>(key, matcher);
                      break ENTRY_SET_LOOP;
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
    return returnValue;
  }

  /**
   * A simple tuple that combines a {@link ResourceBundle}, a {@link
   * String} key and a {@link Matcher}.
   *
   * @author <a href="http://about.me/lairdnelson"
   * target="_parent">Laird Nelson</a>
   *
   * @see MessageFactory#getSelector(List)
   */
  private static final class Selector<T> {

    /**
     * The {@link Selector}'s key.  This field is never {@code null}.
     */
    private final ResourceBundleKey key;

    /**
     * This {@link Selector}'s {@link Matcher}.  This field is never
     * {@code null}.
     */
    private final Matcher<T> matcher;

    /**
     * Creates a new {@link Selector}.
     *
     * @param key the key portion of this {@link Selector}; must not
     * be {@code null}
     *
     * @param matcher the {@link Matcher} portion of this {@link
     * Selector}; must not be {@code null}
     *
     * @exception IllegalArgumentException if either parameter is
     * {@code null}
     */
    private Selector(final ResourceBundleKey key, final Matcher<T> matcher) {
      super();
      if (key == null) {
        throw new IllegalArgumentException("key", new NullPointerException("key"));
      }
      if (matcher == null) {
        throw new IllegalArgumentException("matcher", new NullPointerException("matcher"));
      }
      this.key = key;
      this.matcher = matcher;
    }

    /**
     * Returns the key of this {@link Selector}.
     *
     * <p>This method never returns {@code null}.</p>
     *
     * @return the non-{@code null} key of this {@link Selector}
     */
    public final ResourceBundleKey getKey() {
      return this.key;
    }

    /**
     * Returns the {@link Matcher} of this {@link Selector}.
     *
     * <p>This method never returns {@code null}.</p>
     *
     * @return the non-{@code null} {@link Matcher} portion of this
     * {@link Selector}
     */
    public final Matcher<T> getMatcher() {
      return this.matcher;
    }

    /**
     * Returns a hashcode for this {@link Selector}.
     *
     * @return a hashcode for this {@link Selector}
     */
    @Override
    public int hashCode() {
      int result = 17;

      final Object key = this.getKey();
      int c;
      if (key == null) {
        c = 0;
      } else {
        c = key.hashCode();
      }
      result = 37 * result + c;

      final Object matcher = this.getMatcher();
      if (matcher == null) {
        c = 0;
      } else {
        c = matcher.hashCode();
      }
      result = 37 * result + c;

      return result;
    }

    /**
     * Returns {@code true} if the supplied {@link Object} is equal to
     * this {@link Selector}; {@code false} otherwise.
     *
     * @param other the {@link Object} to compare; may be {@code null}
     *
     * @return {@code true} if the supplied {@link Object} is equal to
     * this {@link Selector}; {@code false} otherwise
     */
    @Override
    public boolean equals(final Object other) {
      if (other == this) {
        return true;
      } else if (other != null && this.getClass().equals(other.getClass())) {
        final Selector<?> him = (Selector<?>)other;
        final Object key = this.getKey();
        if (key == null) {
          if (him.getKey() != null) {
            return false;
          }
        } else if (!key.equals(him.getKey())) {
          return false;
        }

        final Object matcher = this.getMatcher();
        if (matcher == null) {
          if (him.getMatcher() != null) {
            return false;
          }
        } else if (!matcher.equals(him.getMatcher())) {
          return false;
        }

        return true;
      } else {
        return false;
      }
    }

  }

}
