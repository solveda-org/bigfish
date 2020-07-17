package shipping;

import javolution.util.FastList;

orderBy = ["partyId"];

List contentList = FastList.newInstance();
contentList = delegator.findList("PartyGroup",null, null, orderBy, null, false);
context.resultList = contentList;