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
package com.edugility.splain.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.Serializable; // for javadoc only

import java.text.ParseException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;
import java.util.Set;

import com.edugility.objexj.Pattern;

import com.edugility.splain.MessageFactory;
import com.edugility.splain.ResourceBundleKey;

/**
 * Creates {@link MessageFactory} instances from an underlying {@link
 * Reader}.
 *
 * @author <a href="http://about.me/lairdnelson">Laird Nelson</a>
 */
public class MessageFactoryReader implements Closeable {

  /**
   * This class' version for serialization purposes.
   *
   * @see Serializable
   */
  private static final long serialVersionUID = 1L;

  /**
   * This platform's line separator.  This field is never {@code
   * null}.
   */
  private static final String LS = System.getProperty("line.separator", "\n");

  /**
   * A state that the internal parser can be in.
   *
   * @author <a href="http://about.me/lairdnelson"
   * target="_parent">Laird Nelson</a>
   */
  private enum State {

    /**
     * A normal state.  The parser will be expecting comment lines or
     * lines describing patterns.
     */
    NORMAL,

    /**
     * A state in which the parser will be looking for patterns or
     * comments.
     */
    MATCHERS,

    /**
     * A state in which the parser will attempt to read the message
     * key that is affiliated with a set of patterns.
     */
    MESSAGE
  }

  /**
   * The {@link LineNumberReader} that is responsible for reading the
   * text to be parsed.  This field is never {@code null}.
   */
  private final LineNumberReader reader;

  /**
   * The {@link Locale} in effect for localization.  This field is
   * never {@code null}.
   */
  private final Locale locale;

  /**
   * A {@link ResourceBundle.Control} for loading {@link
   * ResourceBundle}s.  This field may be {@code null}.
   */
  private final Control control;

  public MessageFactoryReader(final Reader reader) {
    this(reader, Locale.getDefault(), Control.getControl(Control.FORMAT_DEFAULT));
  }

  public MessageFactoryReader(final Reader reader, final Locale locale) {
    this(reader, locale, Control.getControl(Control.FORMAT_DEFAULT));
  }

  /**
   * Creates a new {@link MessageFactoryReader}.
   *
   * @param reader the {@link Reader} to read from; must not be {@code
   * null}
   *
   * @param locale the {@link Locale} to use for localization; if
   * {@code null} then the {@linkplain Locale#getDefault() default
   * <code>Locale</code>} will be used instead
   *
   * @exception IllegalArgumentException if {@code reader} is {@code
   * null}
   */
  public MessageFactoryReader(final Reader reader, final Locale locale, final Control rbControl) {
    super();
    if (reader == null) {
      throw new IllegalArgumentException("reader", new NullPointerException("reader"));
    }
    if (reader instanceof LineNumberReader) {
      this.reader = (LineNumberReader)reader;
    } else {
      this.reader = new LineNumberReader(reader);
    }
    if (locale == null) {
      this.locale = Locale.getDefault();
    } else {
      this.locale = locale;
    }
    assert this.locale != null;
    this.control = rbControl;
  }

  /**
   * Returns the {@link Locale} to use for localization.
   *
   * @return a non-{@code null} {@link Locale}
   */
  public Locale getLocale() {
    return this.locale;
  }

  public Control getControl() {
    return this.control;
  }

  /**
   * Reads from this {@link MessageFactoryReader}'s {@linkplain
   * #MessageFactoryReader(Reader) affiliated <tt>Reader</tt>} and
   * builds a {@link MessageFactoryReader} from the results.
   *
   * @param <T> the type of objects new {@link MessageFactory} instances
   * produced from this method will work with
   *
   * @param rb the {@link ResourceBundle} that will be used to
   * localize the results; may be {@code null}
   *
   * @return a new {@link MessageFactory}; never {@code null}
   *
   * @exception IOException if an input or output error occurs
   *
   * @exception IllegalStateException if parsing could not be
   * completed
   *
   * @exception ParseException if the source code could not be parsed
   */
  public <T> MessageFactory<T> read(final ResourceBundle rb) throws IOException, ParseException {
    assert this.reader != null;
    final MessageFactory<T> mf = new MessageFactory<T>();
    State state = State.NORMAL;
    final Set<Pattern<T>> patterns = new LinkedHashSet<Pattern<T>>();
    StringBuilder message = null;
    String line;
    while ((line = this.reader.readLine()) != null) {
      line = line.trim();

      switch (state) {

        // NORMAL
      case NORMAL:
        if (line.isEmpty() || line.startsWith("#")) {
          break;
        } else if (line.startsWith("--")) {
          throw new IllegalStateException("\"--\" is not permitted here at line " + reader.getLineNumber());
        } else {
          state = State.MATCHERS;
          patterns.add(Pattern.<T>compile(line));
          break;
        }
        // end NORMAL


        // MATCHERS
      case MATCHERS:
        if (line.isEmpty()) {
          throw new IllegalStateException("An empty line is not permitted here at line " + reader.getLineNumber());
        } else if (line.startsWith("--")) {
          state = State.MESSAGE;
        } else if (!line.startsWith("#")) {
          patterns.add(Pattern.<T>compile(line));
        }
        break;
        // end MATCHERS


        // MESSAGE
      case MESSAGE:
        if (line.isEmpty()) {
          if (message != null) {
            assert patterns != null;
            assert !patterns.isEmpty();
            addPatterns(mf, rb, message.toString(), patterns);
            patterns.clear();
            message = null;
            state = State.NORMAL;
          }
        } else {
          if (message == null) {
            message = new StringBuilder();
          } else {
            message.append(LS);
          }
          message.append(line);
        }
        break;
        // end MESSAGE


      default:
        throw new IllegalStateException("Unexpected state: " + state);
      }

      if (message != null && !patterns.isEmpty()) {
        addPatterns(mf, rb, message.toString(), patterns);
        patterns.clear();
        message = null;
      }

    }
    return mf;
  }

  private final <T> void addPatterns(final MessageFactory<T> mf, final ResourceBundle rb, final String message, final Set<Pattern<T>> patterns) {
    if (mf == null) {
      throw new IllegalArgumentException("mf", new NullPointerException("mf"));
    }
    if (message == null) {
      throw new IllegalArgumentException("message", new NullPointerException("message"));
    }
    if (patterns == null) {
      throw new IllegalArgumentException("patterns", new NullPointerException("patterns"));
    }
    if (patterns.isEmpty()) {
      throw new IllegalArgumentException("patterns", new IllegalStateException("patterns.isEmpty()"));
    }
    final ResourceBundleKey rbKey = ResourceBundleKey.valueOf(rb, this.getLocale(), this.getControl(), message);
    assert rbKey != null;
    mf.addPatterns(rbKey, patterns);
  }

  /**
   * Closes this {@link MessageFactoryReader} so that it cannot be
   * used again.
   *
   * @exception IOException if an error was encountered during closing
   */
  @Override
  public void close() throws IOException {
    assert this.reader != null;
    this.reader.close();
  }

}
