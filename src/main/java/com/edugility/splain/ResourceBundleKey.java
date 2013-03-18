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
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;

public final class ResourceBundleKey implements Serializable {
    
  private static final long serialVersionUID = 1L;
    
  private final ResourceBundle rb;
    
  private final String key;
    
  public ResourceBundleKey(final String bundleName, final String key) {
    this(ResourceBundle.getBundle(bundleName), key);
  }

  public ResourceBundleKey(final String bundleName, final Locale locale, final String key) {
    this(ResourceBundle.getBundle(bundleName, locale), key);
  }

  public ResourceBundleKey(final String bundleName, final Locale locale, final ClassLoader loader, final String key) {
    this(ResourceBundle.getBundle(bundleName, locale, loader), key);
  }

  public ResourceBundleKey(final String bundleName, final Locale locale, final Control control, final String key) {
    this(ResourceBundle.getBundle(bundleName, locale, control), key);
  }

  public ResourceBundleKey(final String bundleName, final Control control, final String key) {
    this(ResourceBundle.getBundle(bundleName, control), key);
  }

  public ResourceBundleKey(final String bundleName, Locale locale, ClassLoader loader, Control control, final String key) {
    this(ResourceBundle.getBundle(bundleName, locale, loader, control), key);
  }

  public ResourceBundleKey(final String key) {
    this((ResourceBundle)null, key);
  }

  public ResourceBundleKey(final ResourceBundle rb, final String key) {
    super();
    if (key == null) {
      throw new IllegalArgumentException("key", new NullPointerException("key"));
    }
    if (rb != null) {
      // Trigger a MissingResourceException as early as possible.
      rb.getObject(key);
      assert rb.containsKey(key);
    }
    this.rb = rb;
    this.key = key;
  }

  public final ResourceBundle getResourceBundle() {
    return this.rb;
  }

  public final String getKey() {
    return this.key;
  }

  public final Object getObject() {
    final ResourceBundle rb = this.getResourceBundle();
    final String key = this.getKey();
    if (key == null) {
      throw new IllegalStateException("getKey() == null");
    }
    final Object returnValue;
    if (rb == null) {
      returnValue = key;
    } else {
      returnValue = rb.getObject(key);
    }
    return returnValue;
  }

  @Override
  public final int hashCode() {
    int result = 17;

    final Object rb = this.getResourceBundle();
    if (rb != null) {
      result = result * 37 + rb.hashCode();
    }

    final Object key = this.getKey();
    if (key != null) {
      result = result * 37 + key.hashCode();
    }

    return result;
  }

  @Override
  public final boolean equals(final Object other) {
    if (other == this) {
      return true;
    } else if (other != null && this.getClass().equals(other.getClass())) {
      final ResourceBundleKey him = (ResourceBundleKey)other;
      final ResourceBundle rb = this.getResourceBundle();
      final ResourceBundle hisRb = him.getResourceBundle();
      if (rb == null) {
        if (hisRb != null) {
          return false;
        }
      } else if (hisRb == null) {
        return false;
      } else if (rb != hisRb) {
        final Object keys = rb.keySet();
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


  /*
   * Static methods.
   */


  public static final ResourceBundleKey valueOf(final String key) {
    return valueOf(null, Locale.getDefault(), Control.getControl(Control.FORMAT_DEFAULT), key);
  }

  public static final ResourceBundleKey valueOf(final ResourceBundle rb, String key) {
    return valueOf(rb, Locale.getDefault(), Control.getControl(Control.FORMAT_DEFAULT), key);
  }

  public static final ResourceBundleKey valueOf(Control control, String key) {
    return valueOf(null, Locale.getDefault(), control, key);
  }

  public static final ResourceBundleKey valueOf(final ResourceBundle rb, Control control, String key) {
    return valueOf(rb, Locale.getDefault(), control, key);
  }

  public static final ResourceBundleKey valueOf(Locale locale, String key) {
    return valueOf(null, locale, Control.getControl(Control.FORMAT_DEFAULT), key);
  }

  public static final ResourceBundleKey valueOf(final ResourceBundle rb, Locale locale, String key) {
    return valueOf(rb, locale, Control.getControl(Control.FORMAT_DEFAULT), key);
  }

  public static final ResourceBundleKey valueOf(Locale locale, Control control, String key) {
    return valueOf(null, locale, control, key);
  }

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
    final int slashIndex = key.indexOf('/');
    if (slashIndex >= 0) {
      if (key.length() <= 1) {
        throw new IllegalArgumentException("key.indexOf('/') >= 0 && key.length() <= 1");
      }
      final String bundleName = key.substring(0, slashIndex);
      assert bundleName != null;
      final String bundleKey = key.substring(slashIndex + 1);
      assert bundleKey != null;
      if (bundleKey.isEmpty()) {
        throw new IllegalArgumentException(String.format("key.substring(%d).isEmpty()", slashIndex + 1));
      }
      if (bundleName.isEmpty()) {
        if (defaultResourceBundle != null) {
          if (defaultResourceBundle.containsKey(key)) {
            returnValue = new ResourceBundleKey(defaultResourceBundle, key);
          } else {
            returnValue = new ResourceBundleKey(defaultResourceBundle, bundleKey);
          }
        } else {
          returnValue = new ResourceBundleKey(bundleKey);
        }
      } else {
        final ResourceBundle bundle = ResourceBundle.getBundle(bundleName, locale, control);
        assert bundle != null;
        final String rawBundleKey = String.format("/%s", bundleKey);
        if (bundle.containsKey(rawBundleKey)) {
          returnValue = new ResourceBundleKey(bundle, rawBundleKey);
        } else {
          returnValue = new ResourceBundleKey(bundle, bundleKey);
        }
      }
    } else if (defaultResourceBundle != null) {
      if (defaultResourceBundle.containsKey(key)) {
        returnValue = new ResourceBundleKey(defaultResourceBundle, key);
      } else {
        returnValue = new ResourceBundleKey(key);
      }
    } else {
      returnValue = new ResourceBundleKey(key);
    }
    return returnValue;
  }


}
