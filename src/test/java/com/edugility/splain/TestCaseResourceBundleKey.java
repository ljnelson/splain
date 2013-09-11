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

import java.util.Locale;
import java.util.MissingResourceException;

import org.junit.Test;

import static org.junit.Assert.*;

public class TestCaseResourceBundleKey {

  public TestCaseResourceBundleKey() {
    super();
  }

  @Test
  public void testValueOfSlashWithKeyAndWhitespace() {
    final ResourceBundleKey rbk = ResourceBundleKey.valueOf("a.b.c.d/   key");
    assertNotNull(rbk);
    assertEquals("value", rbk.getObject(Locale.getDefault()));
  }

  @Test
  public void testValueOfWithWhitespace() {
    final ResourceBundleKey rbk = ResourceBundleKey.valueOf("a.b.c.d  /   key");
    assertNotNull(rbk);
    assertEquals("a.b.c.d  /   key", rbk.getObject(Locale.getDefault()));
  }

  @Test
  public void testValueOfGoodBundleNameGoodBundleKey() {
    final ResourceBundleKey rbk = ResourceBundleKey.valueOf("a.b.c.d/key");
    assertNotNull(rbk);
    assertEquals("value", rbk.getObject(Locale.getDefault()));
  }

  @Test
  public void testValueOfBundleKeyOnly() {
    final ResourceBundleKey rbk = ResourceBundleKey.valueOf("xyz");
    assertNotNull(rbk);
    assertEquals("xyz", rbk.getObject(Locale.getDefault()));
  }

  @Test(expected = MissingResourceException.class)
  public void testValueOfGoodBundleNameBadBundleKey() {
    final ResourceBundleKey rbk = ResourceBundleKey.valueOf("a.b.c.d/  nonexistent");
    assertNotNull(rbk);
    rbk.getObject(Locale.getDefault());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testBundleOnlyKey() {
    ResourceBundleKey.valueOf("a.b.c.d/");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testBundleOnlyKeyWithWhitespace() {
    ResourceBundleKey.valueOf("a.b.c.d/   ");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testValueOfSlashOnly() {
    ResourceBundleKey.valueOf("/");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testValueOfEmptyString() {
    ResourceBundleKey.valueOf("");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testValueOfWhitespace() {
    ResourceBundleKey.valueOf("     ");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testValueOfNull() {
    ResourceBundleKey.valueOf(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testValueOfSlashWithKey() {
    ResourceBundleKey.valueOf("/key");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testValueOfWhitespaceSlashWithKey() {
    ResourceBundleKey.valueOf("    /key");
  }

}
