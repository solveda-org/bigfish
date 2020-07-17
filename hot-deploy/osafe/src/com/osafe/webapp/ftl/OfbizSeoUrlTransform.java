/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package com.osafe.webapp.ftl;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;

import com.osafe.control.SeoUrlHelper;

import freemarker.core.Environment;
import freemarker.ext.beans.BeanModel;
import freemarker.ext.beans.StringModel;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateTransformModel;

/**
 * @author Ritu Raj Lakhera
 * 
 */

public class OfbizSeoUrlTransform implements TemplateTransformModel {

    public final static String module = OfbizSeoUrlTransform.class.getName();

    public String getStringArg(Map args, String key)
    {
        Object o = args.get(key);
        if (o instanceof SimpleScalar)
        {
            return ((SimpleScalar) o).getAsString();
        }
        else if (o instanceof StringModel)
        {
            return ((StringModel) o).getAsString();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public Writer getWriter(final Writer out, final Map args) throws TemplateModelException, IOException
    {
        final StringBuilder buf = new StringBuilder();

        return new Writer(out)
        {
            @Override
            public void write(char cbuf[], int off, int len)
            {
                buf.append(cbuf, off, len);
            }

            @Override
            public void flush() throws IOException
            {
                out.flush();
            }

            @Override
            public void close() throws IOException
            {
                try
                {
                    Environment env = Environment.getCurrentEnvironment();
                    BeanModel req = (BeanModel) env.getVariable("request");
                    if (UtilValidate.isNotEmpty(req))
                    {
                        String contentId = getStringArg(args, "contentId");
                        String productId = getStringArg(args, "productId");
                        String productCategoryId = getStringArg(args, "productCategoryId");
                        HttpServletRequest request = (HttpServletRequest) req.getWrappedObject();
                        

                        String seoUrl = "";
                        if (UtilValidate.isNotEmpty(contentId))
                        {
                            seoUrl = makeContentUrl(request, contentId);
                        }
                        else if (UtilValidate.isNotEmpty(productId))
                        {
                            seoUrl = makeProductUrl(request, productId, productCategoryId);
                        }
                        else if (UtilValidate.isNotEmpty(productCategoryId))
                        {
                            seoUrl = makeCategoryUrl(request, productCategoryId);
                        }
                        out.write(seoUrl);
                    }
                }
                catch (TemplateModelException e)
                {
                    throw new IOException(e.getMessage());
                }
            }
        };
    }
    
    /**
     * Make category url according to the configurations.
     * 
     * @return String a category url
     */
    public static String makeCategoryUrl(HttpServletRequest request, String productCategoryId)
    {
        long count = 0;

        Delegator delegator = (Delegator) request.getAttribute("delegator");
        if (UtilValidate.isNotEmpty(delegator))
        {
            try 
            {
                List<GenericValue> productCategoryMembers = EntityUtil.filterByDate(delegator.findByAndCache("ProductCategoryMember", UtilMisc.toMap("productCategoryId", productCategoryId)), true);
                if (UtilValidate.isNotEmpty(productCategoryMembers))
                {
                    count = productCategoryMembers.size();
                }
            }
            catch (GenericEntityException e)
            {
                Debug.logWarning(e.getMessage(), module);
            }
        }
        

        StringBuilder newURL = new StringBuilder();
        if (count > 0)
        {
            newURL.append(SeoUrlHelper.PRODUCT_LIST_REQUEST_URL);
        }
        else
        {
            newURL.append(SeoUrlHelper.CATEGORY_LIST_REQUEST_URL);
        }
        newURL.append("?");
        newURL.append(SeoUrlHelper.CATEGORY_REQUEST);
        newURL.append("=");
        newURL.append(productCategoryId);
        return SeoUrlHelper.makeSeoFriendlyUrl(request, newURL.toString());
    }
    
    /**
     * Make product url according to the configurations.
     * 
     * @return String a product url
     */
    public static String makeProductUrl(HttpServletRequest request, String productId, String productCategoryId)
    {
        if(UtilValidate.isEmpty(productCategoryId))
        {
            Delegator delegator = (Delegator) request.getAttribute("delegator");
            if (UtilValidate.isNotEmpty(delegator))
            {
                try
                {
                    GenericValue category = EntityUtil.getFirst(EntityUtil.filterByDate(delegator.findByAndCache("ProductCategoryMember", UtilMisc.toMap("productId", productId)), true));
                    if (UtilValidate.isNotEmpty(category))
                    {
                        productCategoryId = category.getString("productCategoryId");
                    }
                }
                catch (GenericEntityException e)
                {
                    Debug.logWarning(e, module);
                }
            }
        }
        StringBuilder newURL = new StringBuilder();
        newURL.append(SeoUrlHelper.PRODUCT_DETAIL_REQUEST_URL);
        newURL.append("?");
        newURL.append(SeoUrlHelper.PRODUCT_REQUEST);
        newURL.append("=");
        newURL.append(productId);
        newURL.append("&");
        newURL.append(SeoUrlHelper.CATEGORY_REQUEST);
        newURL.append("=");
        newURL.append(productCategoryId);
        return SeoUrlHelper.makeSeoFriendlyUrl(request, newURL.toString());
    }
    
    /**
     * Make content url according to the configurations.
     * 
     * @return String a content url
     */
    public static String makeContentUrl(HttpServletRequest request, String contentId)
    {
        StringBuilder newURL = new StringBuilder();
        newURL.append(SeoUrlHelper.CONTENT_REQUEST_URL);
        newURL.append("?");
        newURL.append(SeoUrlHelper.CONTENT_REQUEST);
        newURL.append("=");
        newURL.append(contentId);
        return SeoUrlHelper.makeSeoFriendlyUrl(request, newURL.toString());
    }

}
