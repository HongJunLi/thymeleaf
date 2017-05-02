/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2016, The THYMELEAF team (http://www.thymeleaf.org)
 * 
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 * 
 * =============================================================================
 */
package org.thymeleaf.util;

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.thymeleaf.templatemode.TemplateMode;


/**
 * <p>
 *   Utility class containing methods for computing content type-related data.
 * </p>
 * <p>
 *   This class is <strong>internal</strong> and should not be used from users code.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.6
 *
 */
public final class ContentTypeUtils {


    private static final String[] MIME_TYPES_HTML = new String[] {"text/html", "application/xhtml+xml"};
    private static final String[] MIME_TYPES_XML = new String[] {"application/xml"};
    private static final String[] MIME_TYPES_JAVASCRIPT =
            new String[] {"application/javascript", "application/x-javascript", "application/ecmascript",
                          "text/javascript", "text/ecmascript", "application/json"};
    private static final String[] MIME_TYPES_CSS = new String[] {"text/css"};
    private static final String[] MIME_TYPES_TEXT = new String[] {"text/plain"};
    private static final String[] MIME_TYPES_SSE = new String[] {"text/event-stream"};

    private static final String[] FILE_EXTENSIONS_HTML = new String[] {".html", ".htm", ".xhtml"};
    private static final String[] FILE_EXTENSIONS_XML = new String[] {".xml", ".rss", ".atom"};
    private static final String[] FILE_EXTENSIONS_JAVASCRIPT = new String[] {".js", ".json"};
    private static final String[] FILE_EXTENSIONS_CSS = new String[] {".css"};
    private static final String[] FILE_EXTENSIONS_TEXT = new String[] {".txt"};


    private static final Map<String,String> NORMALIZED_MIME_TYPES;
    private static final Map<String,String> MIME_TYPE_BY_FILE_EXTENSION;
    private static final Map<String,TemplateMode> TEMPLATE_MODE_BY_MIME_TYPE;


    static {

        final Map<String,String> normalizedMimeTypes = new HashMap<String, String>(15, 1.0f);
        for (final String type : MIME_TYPES_HTML) {
            normalizedMimeTypes.put(type, MIME_TYPES_HTML[0]);
        }
        for (final String type : MIME_TYPES_XML) {
            normalizedMimeTypes.put(type, MIME_TYPES_XML[0]);
        }
        for (final String type : MIME_TYPES_JAVASCRIPT) {
            normalizedMimeTypes.put(type, MIME_TYPES_JAVASCRIPT[0]);
        }
        for (final String type : MIME_TYPES_CSS) {
            normalizedMimeTypes.put(type, MIME_TYPES_CSS[0]);
        }
        for (final String type : MIME_TYPES_TEXT) {
            normalizedMimeTypes.put(type, MIME_TYPES_TEXT[0]);
        }
        for (final String type : MIME_TYPES_SSE) {
            normalizedMimeTypes.put(type, MIME_TYPES_SSE[0]);
        }
        NORMALIZED_MIME_TYPES = Collections.unmodifiableMap(normalizedMimeTypes);


        final Map<String,String> mimeTypesByExtension = new HashMap<String, String>(15, 1.0f);
        for (final String type : FILE_EXTENSIONS_HTML) {
            mimeTypesByExtension.put(type, MIME_TYPES_HTML[0]);
        }
        for (final String type : FILE_EXTENSIONS_XML) {
            mimeTypesByExtension.put(type, MIME_TYPES_XML[0]);
        }
        for (final String type : FILE_EXTENSIONS_JAVASCRIPT) {
            mimeTypesByExtension.put(type, MIME_TYPES_JAVASCRIPT[0]);
        }
        for (final String type : FILE_EXTENSIONS_CSS) {
            mimeTypesByExtension.put(type, MIME_TYPES_CSS[0]);
        }
        for (final String type : FILE_EXTENSIONS_TEXT) {
            mimeTypesByExtension.put(type, MIME_TYPES_TEXT[0]);
        }
        MIME_TYPE_BY_FILE_EXTENSION = Collections.unmodifiableMap(mimeTypesByExtension);


        final Map<String,TemplateMode> templateModeByMimeType = new HashMap<String,TemplateMode>(8, 1.0f);
        templateModeByMimeType.put(MIME_TYPES_HTML[0], TemplateMode.HTML);
        templateModeByMimeType.put(MIME_TYPES_XML[0], TemplateMode.XML);
        templateModeByMimeType.put(MIME_TYPES_JAVASCRIPT[0], TemplateMode.JAVASCRIPT);
        templateModeByMimeType.put(MIME_TYPES_CSS[0], TemplateMode.CSS);
        templateModeByMimeType.put(MIME_TYPES_TEXT[0], TemplateMode.TEXT);
        TEMPLATE_MODE_BY_MIME_TYPE = Collections.unmodifiableMap(templateModeByMimeType);

    }


    public static boolean isContentTypeHTML(final String contentType) {
        return computeTemplateModeForContentType(contentType) == TemplateMode.HTML;
    }


    public static boolean isContentTypeXML(final String contentType) {
        return computeTemplateModeForContentType(contentType) == TemplateMode.XML;
    }


    public static boolean isContentTypeJavaScript(final String contentType) {
        return computeTemplateModeForContentType(contentType) == TemplateMode.JAVASCRIPT;
    }


    public static boolean isContentTypeCSS(final String contentType) {
        return computeTemplateModeForContentType(contentType) == TemplateMode.CSS;
    }


    public static boolean isContentTypeText(final String contentType) {
        return computeTemplateModeForContentType(contentType) == TemplateMode.TEXT;
    }


    public static boolean isContentTypeSSE(final String contentType) {

        if (contentType == null || contentType.trim().length() == 0) {
            return false;
        }

        final ContentType contentTypeObj = ContentType.parseContentType(contentType);
        if (contentTypeObj == null) {
            return false;
        }

        final String normalisedMimeType = NORMALIZED_MIME_TYPES.get(contentTypeObj.getMimeType());
        if (normalisedMimeType == null) {
            return false;
        }

        return normalisedMimeType.equals(MIME_TYPES_SSE[0]);

    }


    public static TemplateMode computeTemplateModeForContentType(final String contentType) {

        if (contentType == null || contentType.trim().length() == 0) {
            return null;
        }

        final ContentType contentTypeObj = ContentType.parseContentType(contentType);
        if (contentTypeObj == null) {
            return null;
        }

        final String normalisedMimeType = NORMALIZED_MIME_TYPES.get(contentTypeObj.getMimeType());
        if (normalisedMimeType == null) {
            return null;
        }

        return TEMPLATE_MODE_BY_MIME_TYPE.get(normalisedMimeType);

    }


    public static TemplateMode computeTemplateModeForTemplateName(final String templateName) {

        final String fileExtension = computeFileExtensionFromTemplateName(templateName);
        if (fileExtension == null) {
            return null;
        }

        final String mimeType = MIME_TYPE_BY_FILE_EXTENSION.get(fileExtension);
        if (mimeType == null) {
            return null;
        }

        return TEMPLATE_MODE_BY_MIME_TYPE.get(mimeType);

    }


    public static boolean hasRecognizedFileExtension(final String templateName) {

        final String fileExtension = computeFileExtensionFromTemplateName(templateName);
        if (fileExtension == null) {
            return false;
        }

        return MIME_TYPE_BY_FILE_EXTENSION.containsKey(fileExtension);

    }


    public static String computeContentTypeForTemplateName(final String templateName, final Charset charset) {

        final String fileExtension = computeFileExtensionFromTemplateName(templateName);
        if (fileExtension == null) {
            return null;
        }

        final String mimeType = MIME_TYPE_BY_FILE_EXTENSION.get(fileExtension);
        if (mimeType == null) {
            return null;
        }

        final ContentType contentType = ContentType.parseContentType(mimeType);
        if (contentType == null) {
            return null;
        }

        if (charset != null) {
            contentType.setCharset(charset);
        }

        return contentType.toString();

    }


    public static Charset computeCharsetFromContentType(final String contentType) {

        if (contentType == null || contentType.trim().length() == 0) {
            return null;
        }

        final ContentType contentTypeObj = ContentType.parseContentType(contentType);
        if (contentTypeObj == null) {
            return null;
        }

        return contentTypeObj.getCharset();

    }


    private static String computeFileExtensionFromTemplateName(final String templateName) {

        if (templateName == null || templateName.trim().length() == 0) {
            return null;
        }

        final int pointPos = templateName.lastIndexOf('.');
        if (pointPos < 0) {
            // No extension, so nothing to use for resolution
            return null;
        }

        return templateName.substring(pointPos).toLowerCase(Locale.US).trim();

    }


    public static String combineContentTypeAndCharset(final String contentType, final Charset charset) {

        if (charset == null) {
            return contentType;
        }

        if (contentType == null || contentType.trim().length() == 0) {
            return null;
        }

        final ContentType contentTypeObj = ContentType.parseContentType(contentType);
        if (contentTypeObj == null) {
            return null;
        }

        contentTypeObj.setCharset(charset);

        return contentTypeObj.toString();

    }







    private ContentTypeUtils() {
        super();
    }



    static class ContentType {

        private final String PARAMETER_CHARSET = "charset";

        private final String mimeType;
        private final LinkedHashMap<String,String> parameters;


        static ContentType parseContentType(final String contentType) {

            if (contentType == null || contentType.trim().length() == 0) {
                return null;
            }

            final String[] tokens = StringUtils.split(contentType, ";");
            final String mimeType = tokens[0].toLowerCase(Locale.US).trim();

            if (tokens.length == 1) {
                return new ContentType(mimeType, new LinkedHashMap<String,String>(2, 1.0f));
            }

            final LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>(2, 1.0f);
            for (int i = 1; i < tokens.length; i++) {
                final String token = tokens[i].toLowerCase(Locale.US).trim();
                final int equalPos = token.indexOf('=');
                if (equalPos != -1) {
                    parameters.put(token.substring(0, equalPos).trim(), token.substring(equalPos + 1).trim());
                } else {
                    parameters.put(token.trim(), "");
                }
            }

            return new ContentType(mimeType, parameters);

        }


        ContentType(final String mimeType, final LinkedHashMap<String,String> parameters) {
            super();
            this.mimeType = mimeType;
            this.parameters = parameters;
        }

        String getMimeType() {
            return this.mimeType;
        }

        LinkedHashMap<String, String> getParameters() {
            return this.parameters;
        }

        Charset getCharset() {
            final String charsetStr = this.parameters.get(PARAMETER_CHARSET);
            if (charsetStr == null) {
                return null;
            }
            try {
                return Charset.forName(charsetStr);
            } catch (final UnsupportedCharsetException e) {
                return null;
            }
        }

        void setCharset(final Charset charset) {
            if (charset != null) {
                this.parameters.put(PARAMETER_CHARSET, charset.name());
            }
        }

        @Override
        public String toString() {
            final StringBuilder strBuilder = new StringBuilder();
            strBuilder.append(this.mimeType);
            for (final Map.Entry<String,String> parameterEntry : this.parameters.entrySet()) {
                strBuilder.append(';');
                strBuilder.append(parameterEntry.getKey());
                strBuilder.append('=');
                strBuilder.append(parameterEntry.getValue());
            }
            return strBuilder.toString();
        }

    }

    
}