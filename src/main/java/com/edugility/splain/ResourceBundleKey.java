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
 * A tuple consisting of a {@link ResourceBundle} and a {@linkplain
 * #getKey() key} into that {@link ResourceBundle}.
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
   * The {@link ResourceBundle} component of this {@link
   * ResourceBundleKey}.
   *
   * <p>This field may be {@code null}.</p>
   */
  private final ResourceBundle resourceBundle;

  /**
   * The name used by calling code to retrieve the {@link
   * ResourceBundle} {@linkplain #resourceBundle used} by this class.
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
    

  /*
   * Constructors
   */


  /**
   * Creates a new {@link ResourceBundleKey}.
   *
   * <p>This constructor calls the {@link
   * #ResourceBundleKey(ResourceBundle, String, String)}
   * constructor.</p>
   *
   * @param value what would normally be a {@linkplain
   * ResourceBundle#getObject(String) key into} a {@link
   * ResourceBundle}, but since no {@link ResourceBundle} can be
   * supplied to this constructor, the actual value that will be
   * returned by the {@link #getObject()} method; must not be {@code
   * null}
   *
   * @exception IllegalArgumentException if {@code value} is {@code
   * null}
   *
   * @see #ResourceBundleKey(ResourceBundle, String, String)
   *
   * @see #valueOf(ResourceBundle, Locale, ResourceBundle.Control, String)
   */
  public ResourceBundleKey(final String value) {
    this(null, null, value);
  }

  /**
   * Creates a new {@link ResourceBundleKey}.
   *
   * <p>This constructor calls the {@link
   * #ResourceBundleKey(ResourceBundle, String, String)}
   * constructor.</p>
   *
   * @param resourceBundle the {@link ResourceBundle} that will be
   * returned by the {@link #getResourceBundle()} method; may be
   * {@code null}
   *
   * @param key the key identifying an {@linkplain
   * ResourceBundle#getObject(String) value} within the supplied
   * {@link ResourceBundle} (if the supplied {@link ResourceBundle} is
   * non-{@code null}; must not be {@code null}
   *
   * @exception IllegalArgumentException if {@code key} is {@code
   * null}
   *
   * @exception MissingResourceException if {@code resourceBundle} is
   * non-{@code null} and does not {@linkplain
   * ResourceBundle#containsKey(String) contain} the supplied {@code
   * key}
   *
   * @see #ResourceBundleKey(ResourceBundle, String, String)
   *
   * @see #valueOf(ResourceBundle, Locale, ResourceBundle.Control, String)
   */
  public ResourceBundleKey(final ResourceBundle resourceBundle, final String key) {
    this(resourceBundle, null, key);
  }

  /**
   * Creates a new {@link ResourceBundleKey}.
   *
   * @param resourceBundle the {@link ResourceBundle} that will be
   * returned by the {@link #getResourceBundle()} method; may be
   * {@code null}
   *
   * @param resourceBundleName the name of the supplied {@code
   * resourceBundle}; may be {@code null}
   *
   * @param key the key identifying an {@linkplain
   * ResourceBundle#getObject(String) value} within the supplied
   * {@link ResourceBundle} (if the supplied {@link ResourceBundle} is
   * non-{@code null}; must not be {@code null}
   *
   * @exception IllegalArgumentException if {@code key} is {@code
   * null}
   *
   * @exception MissingResourceException if {@code resourceBundle} is
   * non-{@code null} and does not {@linkplain
   * ResourceBundle#containsKey(String) contain} the supplied {@code
   * key}
   *
   * @see #valueOf(ResourceBundle, Locale, ResourceBundle.Control, String)
   */
  public ResourceBundleKey(final ResourceBundle resourceBundle, final String resourceBundleName, final String key) {
    super();
    if (key == null) {
      throw new IllegalArgumentException("key", new NullPointerException("key"));
    }
    if (resourceBundle != null) {
      // Trigger a MissingResourceException as early as possible.
      resourceBundle.getObject(key);
      assert resourceBundle.containsKey(key);
    }
    this.resourceBundleName = resourceBundleName;
    this.resourceBundle = resourceBundle;
    this.key = key;
  }

  /**
   * Returns the {@link ResourceBundle} component of this {@link
   * ResourceBundleKey}.
   *
   * <p>This method may return {@code null}.</p>
   *
   * @return the {@link ResourceBundle} component of this {@link
   * ResourceBundleKey}, or {@code null}
   *
   * @see #ResourceBundleKey(ResourceBundle, String, String)
   */
  private final ResourceBundle getResourceBundle() {
    return this.resourceBundle;
  }

  /**
   * Returns the name of the {@linkplain #getResourceBundle()
   * affiliated <code>ResourceBundle</code>}, or {@code null} if no
   * name was supplied at {@linkplain
   * #ResourceBundleKey(ResourceBundle, String, string) construction
   * time}.
   *
   * <p>This method may return {@code null}.</p>
   *
   * @return the name of this {@link ResourceBundleKey}'s {@linkplain
   * #getResourceBundle() affiliated <code>ResourceBundle</code>}, or
   * {@code null}
   *
   * @see #ResourceBundleKey(ResourceBundle, String, String)
   */
  private final String getResourceBundleName() {
    return this.resourceBundleName;
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
   * Returns the {@linkplain ResourceBundle#getObject(String) value}
   * corresponding to this {@link ResourceBundleKey}.
   *
   * <p>This method never returns {@code null}.</p>
   *
   * @return a non-{@code null} {@link Object} representing the value
   * corresponding to this {@link ResourceBundleKey}
   *
   * @see ResourceBundle#getObject(String)
   */
  public final Object getObject() {
    final Object returnValue;
    final ResourceBundle resourceBundle = this.getResourceBundle();
    if (resourceBundle == null) {
      returnValue = this.getKey();
    } else {
      returnValue = resourceBundle.getObject(this.getKey());
    }
    return returnValue;
  }

  /**
   * Returns a hashcode for this {@link ResourceBundleKey}.
   *
   * @return a hashcode for this {@link ResourceBundleKey}
   */
  @Override
  public final int hashCode() {
    int result = 17;

    final ResourceBundle resourceBundle = this.getResourceBundle();
    if (resourceBundle != null) {
      final Object keySet = resourceBundle.keySet();
      if (keySet != null) {
        result = result * 37 + keySet.hashCode();
      }
    }

    final Object key = this.getKey();
    if (key != null) {
      result = result * 37 + key.hashCode();
    }

    return result;
  }

  /**
   * Returns {@code true} if the supplied {@link Object} is equal to
   * this {@link ResourceBundleKey}; {@code false} otherwise.
   *
   * <p>For this method to return {@code true}, the supplied {@link
   * Object} must {@linkplain Object#getClass() have a
   * <code>Class</code>} that is equal to this {@link
   * ResourceBundleKey}'s {@link Object#getClass() Class}, and its
   * {@linkplain #getKey() key} and {@linkplain #getResourceBundle()
   * associated <code>ResourceBundle</code>}'s {@link
   * ResourceBundle#keySet() keySet} must be equal.</p>
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
      final ResourceBundle resourceBundle = this.getResourceBundle();
      final ResourceBundle hisRb = him.getResourceBundle();
      if (resourceBundle == null) {
        if (hisRb != null) {
          return false;
        }
      } else if (hisRb == null) {
        return false;
      } else if (!resourceBundle.equals(hisRb)) {
        final Object keys = resourceBundle.keySet();
        if (keys == null) {
          if (hisRb.keySet() != null) {
            return false;
          }
        } else if (!keys.equals(hisRb.keySet())) {
          return false;
        }
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
   * #getObject()} method.
   * 
   * <p>This method never returns {@code null}.</p>
   *
   * <p>The format of the {@link String} representation returned by
   * this method may change at any time.</p>
   *
   * @return a non-{@code null} {@link String}
   *
   * @see #valueOf(ResourceBundle, Locale, ResourceBundle.Control, String)
   */
  @Override
  public final String toString() {
    final StringBuilder sb = new StringBuilder();
    final Object resourceBundleName = this.getResourceBundleName();
    if (resourceBundleName == null) {
      final Object resourceBundle = this.getResourceBundle();
      if (resourceBundle != null) {
        sb.append(resourceBundle).append("/");
      }
    } else {
      sb.append(resourceBundleName).append("/");
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
   * Calls the {@link #valueOf(ResourceBundle, Locale, ResourceBundle.Control,
   * String)} method, passing {@code null}, {@link Locale#getDefault()
   * Locale.getDefault()}, {@link Control#getControl(List)
   * Control.getControl(Control.FORMAT_DEFAULT)}, and {@code key}, and
   * returns its results.
   *
   * <p>This method never returns {@code null}.</p>
   *
   * @param key the key to parse; must not be {@code null}
   *
   * @return a new {@link ResourceBundleKey}; never {@code null}
   * @exception IllegalArgumentException if {@code key} is {@code null}
   *
   * @exception MissingResourceException if a {@link ResourceBundle}
   * could not be loaded or a resource within a {@link ResourceBundle}
   * could not be found
   *
   * @see #valueOf(ResourceBundle, Locale, ResourceBundle.Control, String)
   */
  public static final ResourceBundleKey valueOf(final String key) {
    return valueOf(null, Locale.getDefault(), Control.getControl(Control.FORMAT_DEFAULT), key);
  }

  /**
   * Calls the {@link #valueOf(ResourceBundle, Locale, ResourceBundle.Control,
   * String)} method, passing {@code defaultResourceBundle}, {@link
   * Locale#getDefault() Locale.getDefault()}, {@link
   * Control#getControl(List)
   * Control.getControl(Control.FORMAT_DEFAULT)}, and {@code key}, and
   * returns its results.
   *
   * <p>This method never returns {@code null}.</p>
   *
   * @param defaultResourceBundle the {@link ResourceBundle} that is
   * used to resolve relative keys; may be {@code null}
   *
   * @param key the key to parse; must not be {@code null}
   *
   * @return a new {@link ResourceBundleKey}; never {@code null}
   * @exception IllegalArgumentException if {@code key} is {@code null}
   *
   * @exception MissingResourceException if a {@link ResourceBundle}
   * could not be loaded or a resource within a {@link ResourceBundle}
   * could not be found
   *
   * @see #valueOf(ResourceBundle, Locale, ResourceBundle.Control, String)
   */
  public static final ResourceBundleKey valueOf(final ResourceBundle defaultResourceBundle, String key) {
    return valueOf(defaultResourceBundle, Locale.getDefault(), Control.getControl(Control.FORMAT_DEFAULT), key);
  }

  /**
   * Calls the {@link #valueOf(ResourceBundle, Locale, ResourceBundle.Control,
   * String)} method, passing {@code null}, {@link
   * Locale#getDefault() Locale.getDefault()}, {@code control}, and
   * {@code key}, and returns its results.
   *
   * <p>This method never returns {@code null}.</p>
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
   * @exception IllegalArgumentException if {@code key} is {@code null}
   *
   * @exception MissingResourceException if a {@link ResourceBundle}
   * could not be loaded or a resource within a {@link ResourceBundle}
   * could not be found
   *
   * @see #valueOf(ResourceBundle, Locale, ResourceBundle.Control, String)
   */
  public static final ResourceBundleKey valueOf(Control control, String key) {
    return valueOf(null, Locale.getDefault(), control, key);
  }

  /**
   * Calls the {@link #valueOf(ResourceBundle, Locale, ResourceBundle.Control,
   * String)} method, passing {@code defaultResourceBundle}, {@link
   * Locale#getDefault() Locale.getDefault()}, {@code control}, and
   * {@code key}, and returns its results.
   *
   * <p>This method never returns {@code null}.</p>
   *
   * @param defaultResourceBundle the {@link ResourceBundle} that is
   * used to resolve relative keys; may be {@code null}
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
   * @exception IllegalArgumentException if {@code key} is {@code null}
   *
   * @exception MissingResourceException if a {@link ResourceBundle}
   * could not be loaded or a resource within a {@link ResourceBundle}
   * could not be found
   *
   * @see #valueOf(ResourceBundle, Locale, ResourceBundle.Control, String)
   */
  public static final ResourceBundleKey valueOf(final ResourceBundle defaultResourceBundle, Control control, String key) {
    return valueOf(defaultResourceBundle, Locale.getDefault(), control, key);
  }

  /**
   * Calls the {@link #valueOf(ResourceBundle, Locale, ResourceBundle.Control,
   * String)} method, passing {@code null}, {@code locale}, {@link
   * Control#getControl(List)
   * Control.getControl(Control.FORMAT_DEFAULT)} and {@code key}, and
   * returns its results.
   *
   * <p>This method never returns {@code null}.</p>
   *
   * @param locale the {@link Locale} to use when {@linkplain
   * ResourceBundle#getBundle(String, Locale, ClassLoader, Control)
   * loading <code>ResourceBundle</code>s}; if {@code null} then the
   * return value of {@link Locale#getDefault()} is used instead
   *
   * @param key the key to parse; must not be {@code null}
   *
   * @return a new {@link ResourceBundleKey}; never {@code null}
   * @exception IllegalArgumentException if {@code key} is {@code null}
   *
   * @exception MissingResourceException if a {@link ResourceBundle}
   * could not be loaded or a resource within a {@link ResourceBundle}
   * could not be found
   *
   * @see #valueOf(ResourceBundle, Locale, ResourceBundle.Control, String)
   */
  public static final ResourceBundleKey valueOf(Locale locale, String key) {
    return valueOf(null, locale, Control.getControl(Control.FORMAT_DEFAULT), key);
  }

  /**
   * Calls the {@link #valueOf(ResourceBundle, Locale, ResourceBundle.Control,
   * String)} method, passing {@code defaultResourceBundle}, {@code locale}, {@link
   * Control#getControl(List)
   * Control.getControl(Control.FORMAT_DEFAULT)} and {@code key}, and
   * returns its results.
   *
   * <p>This method never returns {@code null}.</p>
   *
   * @param defaultResourceBundle the {@link ResourceBundle} that is
   * used to resolve relative keys; may be {@code null}
   *
   * @param locale the {@link Locale} to use when {@linkplain
   * ResourceBundle#getBundle(String, Locale, ClassLoader, Control)
   * loading <code>ResourceBundle</code>s}; if {@code null} then the
   * return value of {@link Locale#getDefault()} is used instead
   *
   * @param key the key to parse; must not be {@code null}
   *
   * @return a new {@link ResourceBundleKey}; never {@code null}
   * @exception IllegalArgumentException if {@code key} is {@code null}
   *
   * @exception MissingResourceException if a {@link ResourceBundle}
   * could not be loaded or a resource within a {@link ResourceBundle}
   * could not be found
   *
   * @see #valueOf(ResourceBundle, Locale, ResourceBundle.Control,
   * String)
   */
  public static final ResourceBundleKey valueOf(final ResourceBundle defaultResourceBundle, Locale locale, String key) {
    return valueOf(defaultResourceBundle, locale, Control.getControl(Control.FORMAT_DEFAULT), key);
  }

  /**
   * Calls the {@link #valueOf(ResourceBundle, Locale,
   * ResourceBundle.Control, String)} method, passing {@code null},
   * {@code locale}, {@code control} and {@code key}, and returns its
   * results.
   *
   * <p>This method never returns {@code null}.</p>
   *
   * @param locale the {@link Locale} to use when {@linkplain
   * ResourceBundle#getBundle(String, Locale, ClassLoader, Control)
   * loading <code>ResourceBundle</code>s}; if {@code null} then the
   * return value of {@link Locale#getDefault()} is used instead
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
   * @exception IllegalArgumentException if {@code key} is {@code null}
   *
   * @exception MissingResourceException if a {@link ResourceBundle}
   * could not be loaded or a resource within a {@link ResourceBundle}
   * could not be found
   *
   * @see #valueOf(ResourceBundle, Locale, ResourceBundle.Control, String)
   */
  public static final ResourceBundleKey valueOf(Locale locale, Control control, String key) {
    return valueOf(null, locale, control, key);
  }

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
   * <p>This method never returns {@code null}.</p>
   *
   * <p>The supplied {@code key} is checked to see if it is {@code
   * null}.  If so, an {@link IllegalArgumentException} is thrown.</p>
   *
   * <p>Next, the key is {@linkplain String#trim() trimmed}.  If this
   * results in an {@linkplain String#isEmpty() empty key}, an {@link
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
   * segments are separated with periods ("{@code .}").</li>
   *
   * <li>A <em>bundle key</em> component, separated from the bundle
   * name with a solidus ("{@code /}"; the solidus is part of neither
   * the bundle name nor the bundle key).  A bundle key is an
   * arbitrary {@link String} that is legal for supplying to the
   * {@link ResourceBundle#getObject(String)} method.</li>
   *
   * </ol>
   *
   * <p>The bundle name component of the supplied {@code key} is
   * optional.  In this case, if the supplied and {@linkplain
   * String#trim() trimmed} {@code key} begins with a solidus ("{@code
   * /}"), then the remainder of the key&mdash;the bundle key&mdash;is
   * taken to be a key that {@linkplain
   * ResourceBundle#containsKey(String) is contained} by the supplied
   * {@code defaultResourceBundle} parameter.</p>
   *
   * <p>If the bundle key after this parsing and normalization is
   * {@linkplain String#isEmpty() empty}, then an {@code
   * IllegalArgumentException} is thrown.</p>
   *
   * <p>If the bundle name after this parsing and normalization is
   * {@linkplain String#isEmpty() empty}, then the {@link
   * ResourceBundle} that will be used to resolve bundle keys will be
   * the supplied {@code defaultResourceBundle} parameter.</p>
   *
   * <p>If for any reason the supplied {@code key} cannot be parsed
   * into a (possibly empty) bundle name and key component divided by
   * a solidus:</p>
   *
   * <ul>
   *
   * <li>The code checks to see if the supplied {@code
   * defaultResourceBundle} parameter {@linkplain
   * ResourceBundle#containsKey(String) contains} the {@linkplain
   * String#trim() trimmed} {@code key} parameter.</li>
   *
   * <li>If it does, then the equivalent of {@link
   * #ResourceBundleKey(ResourceBundle, String) new
   * ResourceBundleKey(defaultResourceBundle, key.trim())} is
   * returned.  If it does not, then the equivalent of {@link
   * #ResourceBundleKey(String) new ResourceBundleKey(key.trim())} is
   * returned.</li>
   *
   * </ul>
   *
   * @param defaultResourceBundle the {@link ResourceBundle} that is
   * used to resolve relative keys; may be {@code null}
   *
   * @param locale the {@link Locale} to use when {@linkplain
   * ResourceBundle#getBundle(String, Locale, ClassLoader, Control)
   * loading <code>ResourceBundle</code>s}; if {@code null} then the
   * return value of {@link Locale#getDefault()} is used instead
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
  public static final ResourceBundleKey valueOf(final ResourceBundle defaultResourceBundle, Locale locale, Control control, String key) {
    if (key == null) {
      throw new IllegalArgumentException("key", new NullPointerException("key"));
    } else if (key.isEmpty()) {
      throw new IllegalArgumentException("key.isEmpty()");
    }
    key = key.trim();
    assert key != null;
    if (key.isEmpty()) {
      throw new IllegalArgumentException("key.trim().isEmpty()");
    }
    if (locale == null) {
      locale = Locale.getDefault();
    }
    if (control == null) {
      control = Control.getControl(Control.FORMAT_DEFAULT);
    }
    final ResourceBundleKey returnValue;

    State state = State.START;

    String bundleName = null;
    String bundleKey = null;

    final char[] keyChars = key.toCharArray();
    assert keyChars != null;
    assert keyChars.length > 0;

    final StringBuilder sb = new StringBuilder();

    for (int i = 0; i < keyChars.length; i++) {
      final char c = keyChars[i];
      switch (state) {

        // START
      case START:
        switch (c) {
        case '/':
          bundleName = "";
          if (keyChars.length == 1) {
            throw new IllegalArgumentException("key.equals(\"/\")");
          }
          state = State.BUNDLE_KEY_START;
          break;
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
          sb.append(c);
          state = State.BUNDLE_NAME_SEGMENT_START;
          break;
        case '/':
          bundleName = sb.toString();
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
      break;
    default:
      throw new IllegalArgumentException(String.format("Malformed key: %s", key));
    }

    assert bundleKey != null;    
    assert !bundleKey.isEmpty();
    
    if (bundleName == null) {
      if (defaultResourceBundle != null && defaultResourceBundle.containsKey(bundleKey)) {
        returnValue = new ResourceBundleKey(defaultResourceBundle, bundleKey);
      } else {
        returnValue = new ResourceBundleKey(bundleKey);
      }
    } else if (bundleName.isEmpty()) {
      returnValue = new ResourceBundleKey(defaultResourceBundle, bundleKey);
    } else {
      final ResourceBundle rb = ResourceBundle.getBundle(bundleName, locale, control);
      assert rb != null;
      returnValue = new ResourceBundleKey(rb, bundleName, bundleKey);
    }
    return returnValue;
  }

}
