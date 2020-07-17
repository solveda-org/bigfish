package admin;

import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.string.*;


import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilMisc;

//SERVER_HIT Entity
entityList = FastList.newInstance();
entityMap = FastMap.newInstance();
entityMap.put("entityDisplayName", uiLabelMap.ServerHitEntityLabel);
entityMap.put("entityDBName", "ServerHit");
entityMap.put("entityRowCount", delegator.findCountByCondition("ServerHit",null, null, null));
entityMap.put("helperText", uiLabelMap.ServerHitEntityHelperInfo);
entityList.add(entityMap);

//SERVER_HIT_BIN Entity
entityMap = FastMap.newInstance();
entityMap.put("entityDisplayName", uiLabelMap.ServerHitBinEntityLabel);
entityMap.put("entityDBName", "ServerHitBin");
entityMap.put("entityRowCount", delegator.findCountByCondition("ServerHitBin",null, null, null));
entityMap.put("helperText", uiLabelMap.ServerHitBinEntityHelperInfo);
entityList.add(entityMap);

//VISIT entity
entityMap = FastMap.newInstance();
entityMap.put("entityDisplayName", uiLabelMap.VisitEntityLabel);
entityMap.put("entityDBName", "Visit");
entityMap.put("entityRowCount", delegator.findCountByCondition("Visit",null, null, null));
entityMap.put("helperText", uiLabelMap.VisitEntityHelperInfo);
entityList.add(entityMap);

//VISITOR entity
entityMap = FastMap.newInstance();
entityMap.put("entityDisplayName", uiLabelMap.VisitorEntityLabel);
entityMap.put("entityDBName", "Visitor");
entityMap.put("entityRowCount", delegator.findCountByCondition("Visitor",null, null, null));
entityMap.put("helperText", uiLabelMap.VisitorEntityHelperInfo);
entityList.add(entityMap);

//CART_ABANDONED_LINE entity
entityMap = FastMap.newInstance();
entityMap.put("entityDisplayName", uiLabelMap.CartAbandonedLineEntityLabel);
entityMap.put("entityDBName", "CartAbandonedLine");
entityMap.put("entityRowCount", delegator.findCountByCondition("CartAbandonedLine",null, null, null));
entityMap.put("helperText", uiLabelMap.CartAbandonedLineEntityHelperInfo);
entityList.add(entityMap);

//SHOPPING_LIST entity
entityMap = FastMap.newInstance();
entityMap.put("entityDisplayName", uiLabelMap.ShoppingListEntityLabel);
entityMap.put("entityDBName", "ShoppingList");
entityMap.put("entityRowCount", delegator.findCountByCondition("ShoppingList",null, null, null));
entityMap.put("helperText", uiLabelMap.ShoppingListEntityHelperInfo);
entityList.add(entityMap);

//SHOPPING_LIST_ITEM entity
entityMap = FastMap.newInstance();
entityMap.put("entityDisplayName", uiLabelMap.ShoppingListItemEntityLabel);
entityMap.put("entityDBName", "ShoppingListItem");
entityMap.put("entityRowCount", delegator.findCountByCondition("ShoppingListItem",null, null, null));
entityMap.put("helperText", uiLabelMap.ShoppingListItemEntityHelperInfo);
entityList.add(entityMap);



context.entityRowSizeAnalysisList = UtilMisc.sortMaps(entityList, UtilMisc.toList("entityDisplayName"));

