<div class="displayBox generalInfo">
    <div class="header"><h2>${generalInfoBoxHeading}</h2></div>
    <div class="boxBody">
          ${sections.render('generalInfoBoxBody')}
    </div>
</div>
${sections.render('addressInfoBoxBody')}
<div class="displayListBox noteInfo">
    <div class="header"><h2>${customerNoteInfoBoxHeading!}</h2></div>
    <div class="boxBody">
          ${sections.render('customerNoteBoxBody')!}
    </div>
</div>
<div class="displayBox footerInfo">
    <div class="boxBody">
          ${sections.render('footerBoxBody')}
    </div>
</div>
