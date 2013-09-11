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

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;

/**
 * An identifier of a particular resource within a {@link
 * ResourceBundle}.
 *
 * @author <a href="http://about.me/lairdnelson"
 * target="_parent">Laird Nelson</a>
 *
 * @see ResourceBundle
 */
public final class ResourceBundleKey implements Serializable {


  /*
   * Static fields.
   */


  /**
   * The version of this class for {@linkplain Serializable
   * serialization purposes}.
   */
  private static final long serialVersionUID = 1L;


  /*
   * Instance fields.
   */


  /**
   * The {@link Control} used to {@linkplain
   * ResourceBundle#getBundle(String, Locale, ClassLoader, Control)
   * load} {@link ResourceBundle}s.
   *
   * <p>This field is never {@code null}.</p>
   */
  private final Control control;

  /**
   * The {@linkplain Locale locale}-independent name of the {@link
   * ResourceBundle} into which this {@link ResourceBundleKey}
   * indexes.
   *
   * <p>This field may be {@code null}.</p>
   */
  private final String resourceBundleName;

  /**
   * The key component of this {@link ResourceBundleKey}.
   *
   * <p>This field is never {@code null}.</p>
   */
  private final String key;

  /**
   * The {@link ClassLoader} used to {@linkplain
   * ResourceBundle#getBundle(String, Locale, ClassLoader, Control)
   * load} {@link ResourceBundle}s.
   *
   * <p>This field is never {@code null}.</p>
   */
  private final ClassLoader bundleLoader;


  /*
   * Constructors.
   */


  /**
   * Creates a new {@link ResourceBundleKey} that will return the
   * supplied {@code key} from its {@link #getObject(Locale)} method.
   *
   * <p>This constructor calls the {@link #ResourceBundleKey(String,
   * ClassLoader, ResourceBundle.Control, String)} constructor,
   * passing {@code null} for the first three arguments and the
   * supplied {@code key} for the fourth.</p>
   *
   * @param key the {@link String} to return from the {@link
   * #getObject(Locale)} method; must not be {@code null}
   *
   * @exception IllegalArgumentException if {@code key} is {@code
   * null}
   *
   * @see #ResourceBundleKey(String, ClassLoader,
   * ResourceBundle.Control, String)
   */
  public ResourceBundleKey(final String key) {
    this(null, null, null, key);
  }

  /**
   * Creates a new {@link ResourceBundleKey}.
   *
   * <p>This constructor calls the {@link #ResourceBundleKey(String,
   * ClassLoader, ResourceBundle.Control, String)} constructor,
   * passing the supplied {@code resourceBundleName} as the first
   * argument, null for the second and third and the supplied {@code
   * key} for the fourth.</p>
   *
   * @param resourceBundleName a fully-qualified class name
   * identifying a {@link ResourceBundle}; may be {@code null}
   *
   * @param key a {@link String} identifying a resource within a
   * {@link ResourceBundle} identified by the supplied {@code
   * resourceBundleName} parameter, or, if that parameter is {@code
   * null} or {@linkplain String#isEmpty() empty}, the value to be
   * returned from the {@link #getObject(Locale)} method; must not be
   * {@code null}
   *
   * @exception IllegalArgumentException if {@code key} is {@code
   * null}
   *
   * @see #ResourceBundleKey(String, ClassLoader,
   * ResourceBundle.Control, String)
   */
  public ResourceBundleKey(final String resourceBundleName, final String key) {
    this(resourceBundleName, null, null, key);
  }

  /**
   * Creates a new {@link ResourceBundleKey}.
   *
   * <p>This constructor calls the {@link #ResourceBundleKey(String,
   * ClassLoader, ResourceBundle.Control, String)} constructor,
   * passing the supplied {@code resourceBundleName} as the first
   * argument, null for the second, the supplied {@code control} for
   * the third, and the supplied {@code key} for the fourth.</p>
   *
   * @param resourceBundleName a fully-qualified class name
   * identifying a {@link ResourceBundle}; may be {@code null}
   *
   * @param control a {@link Control} to use for
   * instantiating {@link ResourceBundle}s; may be {@code null} in
   * which case the return value of the {@link
   * Control#getControl(List)} method&mdash;supplied
   * with the {@link Control#FORMAT_DEFAULT}
   * constant&mdash;will be used instead
   *
   * @param key a {@link String} identifying a resource within a
   * {@link ResourceBundle} identified by the supplied {@code
   * resourceBundleName} parameter, or, if that parameter is {@code
   * null} or {@linkplain String#isEmpty() empty}, the value to be
   * returned from the {@link #getObject(Locale)} method; must not be
   * {@code null}
   *
   * @exception IllegalArgumentException if {@code key} is {@code
   * null}
   *
   * @see #ResourceBundleKey(String, ClassLoader,
   * ResourceBundle.Control, String)
   */
  public ResourceBundleKey(final String resourceBundleName, final Control control, final String key) {
    this(resourceBundleName, null, control, key);
  }

  /**
   * Creates a new {@link ResourceBundleKey}.
   *
   * @param resourceBundleName a fully-qualified class name
   * identifying a {@link ResourceBundle}; may be {@code null}
   *
   * @param bundleLoader a {@link ClassLoader} for passing to the
   * {@link ResourceBundle#getBundle(String, Locale, ClassLoader,
   * Control)} method; may be {@code null} in which case the return
   * value of {@link Thread#getContextClassLoader()
   * Thread.currentThread().getContextClassLoader()} will be used
   * instead
   *
   * @param control a {@link Control} to use for instantiating {@link
   * ResourceBundle}s; may be {@code null} in which case the return
   * value of the {@link Control#getControl(List)}
   * method&mdash;supplied with the {@link Control#FORMAT_DEFAULT}
   * constant&mdash;will be used instead
   *
   * @param key a {@link String} identifying a resource within a
   * {@link ResourceBundle} identified by the supplied {@code
   * resourceBundleName} parameter, or, if that parameter is {@code
   * null} or {@linkplain String#isEmpty() empty}, the value to be
   * returned from the {@link #getObject(Locale)} method; must not be
   * {@code null}
   *
   * @exception IllegalArgumentException if {@code key} is {@code
   * null}
   */
  public ResourceBundleKey(final String resourceBundleName, ClassLoader bundleLoader, final Control control, final String key) {
    super();
    if (key == null) {
      throw new IllegalArgumentException("key", new NullPointerException("key"));
    }
    if (control == null) {
      this.control = Control.getControl(Control.FORMAT_DEFAULT);
    } else {
      this.control = control;
    }
    if (bundleLoader == null) {
      bundleLoader = Thread.currentThread().getContextClassLoader();
      if (bundleLoader == null) {
        bundleLoader = this.getClass().getClassLoader();
      }
    }
    assert bundleLoader != null;
    this.bundleLoader = bundleLoader;
    this.resourceBundleName = resourceBundleName;
    this.key = key;
  }


  /*
   * Instance methods.
   */


  /**
   * Returns the {@link Control} that will be used by this {@link
   * ResourceBundleKey} in {@linkplain
   * ResourceBundle#getBundle(String, Locale, ClassLoader, Control)
   * loading} {@link ResourceBundle}s.
   *
   * <p>This method never returns {@code null}.</p>
   *
   * @return a non-{@code null} {@link Control}
   */
  private final Control getControl() {
    final Control returnValue;
    if (this.control == null) {
      returnValue = Control.getControl(Control.FORMAT_DEFAULT);
    } else {
      returnValue = this.control;
    }
    return returnValue;
  }

  /**
   * @exception MissingResourceException
   */
  private final ResourceBundle getResourceBundle(Locale locale) {
    final ResourceBundle returnValue;
    final String resourceBundleName = this.getResourceBundleName();
    if (resourceBundleName == null) {
      returnValue = null;
    } else {
      if (locale == null) {
        locale = Locale.getDefault();
      }
      assert locale != null;
      Control control = this.getControl();
      if (control == null) {
        control = Control.getControl(Control.FORMAT_DEFAULT);
      }
      assert control != null;
      assert this.bundleLoader != null;
      returnValue = ResourceBundle.getBundle(resourceBundleName, locale, this.bundleLoader, control);
    }
    return returnValue;
  }

  /**
   *
   *
   * <p>This method may return {@code null}.</p>
   *
   * @return a {@linkplain Locale locale}-indepdendent name of a
   * {@link ResourceBundle} to load, or {@code null}
   *
   * @see #ResourceBundleKey(Control, String, String)
   */
  private final String getResourceBundleName() {
    return this.resourceBundleName;
  }

  private final ClassLoader getBundleLoader() {
    return this.bundleLoader;
  }

  /**
   * Returns the key component of this {@link ResourceBundleKey}.
   *
   * <p>This method never returns {@code null}.</p>
   *
   * @return the key component of this {@link ResourceBundleKey};
   * never {@code null}
   *
   * @see #ResourceBundleKey(ResourceBundle, String, String)
   */
  private final String getKey() {
    return this.key;
  }

  /**
   * Attempts to {@linkplain ResourceBundle#getBundle(String, Locale,
   * ClassLoader, Control) load} the {@link ResourceBundle} identified
   * by this {@link ResourceBundleKey} and to return the resource in
   * that bundle identified by this {@link ResourceBundleKey}.
   *
   * <p>If this {@link ResourceBundleKey} was constructed with a
   * {@code null} resource bundle name, then the key that was supplied
   * at construction time is simply returned as is.</p>
   *
   * <p>This method may return {@code null} in rare edge cases
   * only.</p>
   *
   * @param locale the {@link Locale} for which an {@link Object}
   * should be retrieved; may be {@code null} in which case the return
   * value of the {@link Locale#getDefault()} method will be used
   * instead
   *
   * @return a resource, or {@code null}
   *
   * @exception MissingResourceException if either an appropriate
   * {@link ResourceBundle} could not be {@linkplain
   * ResourceBundle#getBundle(String, Locale, ClassLoader, Control)
   * loaded} or if the relevant resource within that {@link
   * ResourceBundle} does not {@linkplain
   * ResourceBundle#containsKey(String) exist}
   */
  public final Object getObject(final Locale locale) {
    Object returnValue = null;
    final ResourceBundle rb = this.getResourceBundle(locale);
    final String key = this.getKey();
    if (rb != null && key != null) {
      returnValue = rb.getObject(key);
    } else {
      returnValue = key;
    }
    return returnValue;
  }

  /**
   * Returns a hash code for this {@link ResourceBundleKey}.
   *
   * @return a hash code for this {@link ResourceBundleKey}
   */
  @Override
  public final int hashCode() {
    int result = 17;

    final Object control = this.getControl();
    if (control != null) {
      result = result * 37 + control.hashCode();
    }

    final Object resourceBundleName = this.getResourceBundleName();
    if (resourceBundleName != null) {
      result = result * 37 + resourceBundleName.hashCode();
    }

    final Object key = this.getKey();
    if (key != null) {
      result = result * 37 + key.hashCode();
    }

    final Object bundleLoader = this.getBundleLoader();
    if (bundleLoader != null) {
      result = result * 37 + bundleLoader.hashCode();
    }

    return result;
  }

  /**
   * Returns {@code true} if the supplied {@link Object} is equal to
   * this {@link ResourceBundleKey}; {@code false} otherwise.
   *
   * @param other the {@link Object} to test; may be {@code null}
   *
   * @return {@code true} if and only if the supplied {@link Object}
   * is equal to this {@link ResourceBundleKey}; {@code false}
   * otherwise
   */
  @Override
  public final boolean equals(final Object other) {
    if (other == this) {
      return true;
    } else if (other != null && this.getClass().equals(other.getClass())) {
      final ResourceBundleKey him = (ResourceBundleKey)other;

      final Object key = this.getKey();
      if (key == null) {
        if (him.getKey() != null) {
          return false;
        }
      } else if (!key.equals(him.getKey())) {
        return false;
      }

      final Object resourceBundleName = this.getResourceBundleName();
      if (resourceBundleName == null) {
        if (him.getResourceBundleName() != null) {
          return false;
        }
      } else if (!resourceBundleName.equals(him.getResourceBundleName())) {
        return false;
      }

      final Object control = this.getControl();
      if (control == null) {
        if (him.getControl() != null) {
          return false;
        }
      } else if (!control.equals(him.getControl())) {
        return false;
      }

      final Object bundleLoader = this.getBundleLoader();
      if (bundleLoader == null) {
        if (him.getBundleLoader() != null) {
          return false;
        }
      } else if (!bundleLoader.equals(him.getBundleLoader())) {
        return false;
      }

      return true;
    } else {
      return false;
    }
  }

  /**
   * Returns a non-{@code null} {@link String} representation of this
   * {@link ResourceBundleKey}.  The representation is of the key
   * itself, <em>not</em> of the return value of the {@link
   * #getObject(Locale)} method.
   *
   * <p>This method never returns {@code null}.</p>
   *
   * <p>The format of the {@link String} representation returned by
   * this method may change at any time.</p>
   *
   * @return a non-{@code null} {@link String}
   */
  @Override
  public final String toString() {
    final StringBuilder sb = new StringBuilder();
    final Object resourceBundleName = this.getResourceBundleName();
    if (resourceBundleName != null) {
      sb.append(resourceBundleName);
      sb.append("/");
    }
    final Object key = this.getKey();
    if (key != null) {
      sb.append(key);
    }
    return sb.toString();
  }


  /*
   * Static methods.
   */


  /**
   * A state that the parser used by the {@link
   * #valueOf(ResourceBundle, Locale, ResourceBundle.Control, String)}
   * method may be in.
   *
   * @author <a href="http://about.me/lairdnelson"
   * target="_parent">Laird Nelson</a>
   */
  private static enum State {

    /**
     * The {@link State} the parser is in to start.
     */
    START,

    /**
     * The {@link State} the parser is in when it is expecting
     * {@linkplain Character#isJavaIdentifierStart(int) the beginning
     * of a Java identifier}.
     */
    BUNDLE_NAME_SEGMENT_START,

    /**
     * The {@link State} the parser is in when it is accumulating
     * {@link ResourceBundle} name characters.
     */
    BUNDLE_NAME,

    /**
     * The {@link State} the parser is in when it is about to begin
     * accumulating key characters.
     */
    BUNDLE_KEY_START,

    /**
     * The {@link State} the parser is in when it is accumulating key
     * characters.
     */
    BUNDLE_KEY
  }

  /**
   * Returns a new {@link ResourceBundleKey} that is appropriate for
   * the supplied {@code key}.
   *
   * <p>This method calls the {@link #valueOf(ClassLoader,
   * ResourceBundle.Control, String)} method, passing {@code null} for
   * the first two arguments and the supplied {@code key} for the last
   * one, and returns its result.</p>
   *
   * <p>This method never returns {@code null}.</p>
   *
   * @param key the key to parse; must not be {@code null}
   *
   * @return a non-{@code null} {@link ResourceBundleKey}
   *
   * @exception IllegalArgumentException if {@code key} is {@code null}
   *
   * @exception MissingResourceException if a {@link ResourceBundle}
   * could not be loaded or a resource within a {@link ResourceBundle}
   * could not be found
   */
  public static final ResourceBundleKey valueOf(final String key) {
    return valueOf(null, null, key);
  }

  /**
   * Returns a new {@link ResourceBundleKey} that is appropriate for
   * the supplied {@code key}.
   *
   * <p>This method never returns {@code null}.</p>
   *
   * <p>The supplied {@code key} is checked to see if it is {@code
   * null} or {@linkplain String#isEmpty() empty}.  If so, an {@link
   * IllegalArgumentException} is thrown.</p>
   *
   * <p>The non-{@linkplain String#isEmpty() empty} key is then parsed
   * into a maximum of two components:</p>
   *
   * <ol>
   *
   * <li>A <em>bundle name</em>.  A bundle name is a {@link String} of
   * the form normally supplied to {@link
   * ResourceBundle#getBundle(String, Locale, ClassLoader,
   * Control)}&mdash;that is, a fully-qualified class name whose
   * segments are separated with periods ("{@code .}").  This method
   * uses {@link Character#isJavaIdentifierStart(char)} and its ilk to
   * ensure that the bundle name conforms to the Java Language
   * Specification requirements for class naming.</li>
   *
   * <li>A <em>bundle key</em> component, separated from the bundle
   * name with a solidus ("{@code /}"; the solidus is part of neither
   * the bundle name nor the bundle key).  A bundle key is an
   * arbitrary {@link String} that is legal for supplying to the
   * {@link ResourceBundle#getObject(String)} method.  Leading and
   * trailing whitespace is removed.</li>
   *
   * </ol>
   *
   * <p>If the bundle name after this parsing and normalization does
   * not conform lexically to the requirements of a Java class name,
   * then the entire supplied {@code key} is treated as a simple
   * {@link String}, and no further attempts will be made to resolve
   * it against a {@link ResourceBundle}.</p>
   *
   * <p>If the bundle key after this parsing and normalization is
   * {@linkplain String#isEmpty() empty}, then an {@code
   * IllegalArgumentException} is thrown.</p>
   *
   * <p>Examples:</p>
   *
   * <blockquote><dl>
   *
   * <dt>{@code com.foobar.ErrorMessages/noSuchElement}</dt>
   *
   * <dd>A {@link ResourceBundleKey} whose bundle name is {@code
   * com.foobar.ErrorMessages} and whose bundle key is {@code
   * noSuchElement}.</dd>
   *
   * <dt>{@code com.foobar.ErrorMessages /noSuchElement}</dt>
   *
   * <dd>A {@link ResourceBundleKey} whose bundle name is {@code null}
   * and whose bundle key is {@code com.foobar.ErrorMessages
   * /noSuchElement}.  This is because there is whitespace in the
   * bundle name, so the entire key is treated as a simple {@link
   * String}.</dd>
   *
   * <dt>{@code There was no file found with that name.}</dt>
   *
   * <dd>A {@link ResourceBundleKey} that wraps a simple {@link
   * String} consisting of the text given.</dd>
   *
   * <dt>{@code com.foobar.ErrorMessages/  noSuchElement}</dt>
   *
   * <dd>A {@link ResourceBundleKey} whose bundle name is {@code
   * com.foobar.ErrorMessages} and whose bundle key is {@code
   * noSuchElement}.</dd>
   *
   * </dl></blockquote>
   *
   * @param bundleLoader the {@link ClassLoader} to pass (ultimately)
   * to the {@link ResourceBundle#getBundle(String, Locale,
   * ClassLoader, Control)} method; if {@code null} then the return
   * value of {@link Thread#getContextClassLoader()
   * Thread.currentThread().getContextClassLoader()} will be used
   * instead
   *
   * @param control the {@link Control} to use when {@linkplain
   * ResourceBundle#getBundle(String, Locale, ClassLoader, Control)
   * loading <code>ResourceBundle</code>s}; if {@code null} then the
   * return value of {@link Control#getControl(List)
   * Control.getControl(Control.FORMAT_DEFAULT)} is used instead
   *
   * @param key the key to parse; must not be {@code null}
   *
   * @return a new {@link ResourceBundleKey}; never {@code null}
   *
   * @exception IllegalArgumentException if {@code key} is {@code null}
   *
   * @exception MissingResourceException if a {@link ResourceBundle}
   * could not be loaded or a resource within a {@link ResourceBundle}
   * could not be found
   */
  public static final ResourceBundleKey valueOf(final ClassLoader bundleLoader, final Control control, String key) {
    if (key == null) {
      throw new IllegalArgumentException("key", new NullPointerException("key"));
    } else if (key.isEmpty()) {
      throw new IllegalArgumentException("key.isEmpty()");
    }

    State state = State.START;

    String bundleName = null;
    String bundleKey = null;

    final char[] keyChars = key.toCharArray();
    assert keyChars != null;
    final int keyCharsLength = keyChars.length;
    assert keyCharsLength > 0;

    final StringBuilder sb = new StringBuilder();

    for (int i = 0; i < keyCharsLength; i++) {
      final char c = keyChars[i];
      switch (state) {

        // START
      case START:
        switch (c) {
        case '/':
          throw new IllegalArgumentException(String.format("Malformed key: %s", key));
        default:
          if (Character.isJavaIdentifierStart(c)) {
            state = State.BUNDLE_NAME;
            sb.append(c);
          } else if (!Character.isWhitespace(c)) {
            sb.append(c);
            bundleName = null;
            state = State.BUNDLE_KEY;
          }
          break;
        }
        break;

        // BUNDLE_NAME_SEGMENT_START
      case BUNDLE_NAME_SEGMENT_START:
        sb.append(c);
        if (Character.isJavaIdentifierStart(c)) {
          state = State.BUNDLE_NAME;
        } else {
          bundleName = null;
          state = State.BUNDLE_KEY;
        }
        break;

        // BUNDLE_NAME
      case BUNDLE_NAME:
        switch (c) {
        case '.':
          sb.append('.');
          state = State.BUNDLE_NAME_SEGMENT_START;
          break;
        case '/':
          if (sb.length() <= 0) {
            bundleName = null;
          } else {
            bundleName = sb.toString();
          }
          sb.setLength(0);
          state = State.BUNDLE_KEY_START;
          break;
        default:
          sb.append(c);
          if (!Character.isJavaIdentifierPart(c)) {
            bundleName = null;
            state = State.BUNDLE_KEY;
          }
          break;
        }
        break;

        // BUNDLE_KEY_START
      case BUNDLE_KEY_START:
        if (!Character.isWhitespace(c)) {
          sb.append(c);
          state = State.BUNDLE_KEY;
        }
        break;

        // BUNDLE_KEY
      case BUNDLE_KEY:
        sb.append(c);
        break;

      default:
        throw new IllegalStateException("Unexpected state: " + state);
      }
    }

    switch (state) {
    case BUNDLE_KEY:
      bundleKey = sb.toString();
      sb.setLength(0);
      break;
    case BUNDLE_NAME:
      bundleName = null;
      bundleKey = sb.toString();
      sb.setLength(0);
      break;
    default:
      throw new IllegalArgumentException(String.format("Malformed key: %s", key));
    }

    assert bundleKey != null;
    bundleKey = bundleKey.trim();
    if (bundleKey.isEmpty()) {
      throw new IllegalArgumentException(String.format("Malformed key: %s", key));
    }

    final ResourceBundleKey returnValue = new ResourceBundleKey(bundleName, bundleLoader, control, bundleKey);
    return returnValue;
  }

}
