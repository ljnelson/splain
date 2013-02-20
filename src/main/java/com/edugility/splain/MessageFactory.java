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

import java.io.IOException;
import java.io.Serializable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.LinkedHashSet;

import com.edugility.objexj.Pattern;
import com.edugility.objexj.Matcher;

import org.mvel2.templates.TemplateRuntime;

public class MessageFactory<T> implements Serializable {

  private static final long serialVersionUID = 1L;
  
  private ResourceBundle rb;

  private Map<String, Set<Pattern<T>>> patterns;

  public MessageFactory() {
    super();
  }

  public MessageFactory(final ResourceBundle rb) {
    super();
    this.setResourceBundle(rb);
  }
  
  public boolean addPattern(final String key, final Pattern<T> pattern) {
    if (key == null) {
      throw new IllegalArgumentException("key", new NullPointerException("key"));
    }
    if (pattern == null) {
      throw new IllegalArgumentException("pattern", new NullPointerException("pattern"));
    }
    if (this.patterns == null) {
      this.patterns = new HashMap<String, Set<Pattern<T>>>();
    }
    Set<Pattern<T>> patternSet = this.patterns.get(key);
    if (patternSet == null) {
      patternSet = new LinkedHashSet<Pattern<T>>();
      this.patterns.put(key, patternSet);
    }
    return patternSet.add(pattern);
  }

  public boolean addPatterns(final String key, final Iterable<Pattern<T>> patterns) {
    if (key == null) {
      throw new IllegalArgumentException("key", new NullPointerException("key"));
    }
    if (patterns == null) {
      throw new IllegalArgumentException("patterns", new NullPointerException("patterns"));
    }
    if (this.patterns == null) {
      this.patterns = new HashMap<String, Set<Pattern<T>>>();
    }
    Set<Pattern<T>> patternSet = this.patterns.get(key);
    if (patternSet == null) {
      patternSet = new LinkedHashSet<Pattern<T>>();
      this.patterns.put(key, patternSet);
    }
    boolean returnValue = false;
    for (final Pattern<T> pattern : patterns) {
      if (pattern != null) {
        returnValue = patternSet.add(pattern) || returnValue;
      }
    }
    return returnValue;
  }

  public boolean removePattern(final String key, final Pattern<T> pattern) {
    if (key == null) {
      throw new IllegalArgumentException("key", new NullPointerException("key"));
    }
    if (pattern == null) {
      throw new IllegalArgumentException("pattern", new NullPointerException("pattern"));
    }
    boolean returnValue = false;
    if (this.patterns != null && !this.patterns.isEmpty()) {
      final Set<Pattern<T>> patterns = this.getPatterns(key);
      returnValue = patterns != null && !patterns.isEmpty() && patterns.remove(pattern);
    }
    return returnValue;
  }

  public void removePatterns(final String key) {
    if (key == null) {
      throw new IllegalArgumentException("key", new NullPointerException("key"));
    }
    if (this.patterns != null) {
      this.patterns.remove(key);
    }
  }

  public Set<Pattern<T>> getPatterns(final String key) {
    if (key == null) {
      throw new IllegalArgumentException("key", new NullPointerException("key"));
    }
    final Set<Pattern<T>> set = this.patterns.get(key);
    if (set == null || set.isEmpty()) {
      return Collections.emptySet();
    } else {
      return set;
    }
  }

  public Locale getLocale() {
    Locale locale = null;
    final ResourceBundle rb = this.getResourceBundle();
    if (rb != null) {
      locale = rb.getLocale();
    }
    if (locale == null) {
      locale = Locale.getDefault();
    }
    return locale;
  }

  public ResourceBundle getResourceBundle() {
    return this.rb;
  }

  public void setResourceBundle(final ResourceBundle rb) {
    this.rb = rb;
  }

  /**
   * Formats or transforms the supplied {@code rawMessage} {@link
   * Object} in some way, perhaps by using the information stored as
   * part of the supplied {@link Matcher}.
   *
   * <p>This implementation checks to see if the supplied {@code
   * rawMessage} is an instance of {@link String}.  If so, it is
   * treated as an <a href="">MVEL</a> <a
   * href="http://mvel.codehaus.org/Templating+Guide">template</a>.
   * The template is interpolated using all the capture groups and
   * {@linkplain Matcher#getVariables() variables} that the supplied
   * {@link Matcher} is capable of providing.</p>
   *
   * <p>Specifically, <a
   * href="http://mvel.codehaus.org/MVEL+2.0+Orb+Tags">orb tags</a>
   * may have bodies that reference the {@linkplain
   * Matcher#getVariables() variables contained by the supplied
   * <tt>Matcher</tt>}, as well as {@linkplain Matcher#group(int) its
   * capture groups}.  Capture group variable references begin with
   * the {@code $} character, so the following orb tag would return a
   * {@link List} representing the full match:</p>
   *
   * <blockquote><tt>@{$0}</tt></blockquote>
   *
   * @param rawMessage the unformatted message as returned by the
   * {@link #getObject(List)} method; may be {@code null}
   *
   * @param matcher the {@link Matcher} that was used by the {@link
   * #getObject(List)} method in producing the {@code rawMessage}
   * parameter value; may be {@code null}
   *
   * @return a formatted version of the supplied {@code rawMessage},
   * which may simply be the supplied {@code rawMessage} if no formatting
   * could be performed
   *
   * @see TemplateRuntime
   */
  protected Object format(final Object rawMessage, final Matcher<T> matcher) {
    Object returnValue = rawMessage;
    if (rawMessage instanceof String) {
      final String template = (String)rawMessage;
      if (matcher != null) {
        final Map<Object, Object> variables = new HashMap<Object, Object>();
        final int groupCount = matcher.groupCount();
        for (int i = 0; i < groupCount; i++) {
          variables.put(String.format("$%d", Integer.valueOf(i)), matcher.group(i));
        }
        variables.putAll(matcher.getVariables());
        returnValue = TemplateRuntime.eval(template, variables);
      }
    }
    return returnValue;
  }

  public Object getObject(final List<T> input) {
    final Object returnValue;
    final Selector<T> selector = this.getSelector(input);
    if (selector == null) {
      returnValue = null;
    } else {
      final ResourceBundle rb = this.getResourceBundle();
      if (rb == null) {
        returnValue = selector.getKey();
      } else {
        returnValue = this.format(rb.getObject(selector.getKey()), selector.getMatcher());
      }
    }    
    return returnValue;
  }

  public Object getObject(final List<T> input, final Object defaultValue) {
    Object returnValue = null;
    try {
      returnValue = this.getObject(input);
    } catch (final MissingResourceException oops) {
      // TODO: log
      returnValue = null;
    }
    if (returnValue == null) {
      returnValue = defaultValue;
    }
    return returnValue;
  }

  final Selector<T> getSelector(final List<T> input) {
    Selector<T> returnValue = null;
    if (this.patterns != null && !this.patterns.isEmpty()) {
      final Set<String> keys = this.patterns.keySet();
      if (keys != null && !keys.isEmpty()) {
        KEY_LOOP:
        for (final String key : keys) {
          if (key != null) {
            final Set<Pattern<T>> patterns = this.getPatterns(key);
            if (patterns != null && !patterns.isEmpty()) {
              for (final Pattern<T> pattern : patterns) {
                if (pattern != null) {
                  final Matcher<T> matcher = pattern.matcher(input);
                  assert matcher != null;
                  if (matcher.lookingAt()) {
                    returnValue = new Selector<T>(key, matcher);
                    break KEY_LOOP;
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

  private static final class Selector<T> {

    private final String key;

    private final Matcher<T> matcher;

    private Selector(final String key, final Matcher<T> matcher) {
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

    public final String getKey() {
      return this.key;
    }

    public final Matcher<T> getMatcher() {
      return this.matcher;
    }

  }

}
