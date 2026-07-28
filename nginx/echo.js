function reply(r) {
    r.log("Request:\n" + r.requestText);
    let xml = r.requestText;

    const pos = xml.lastIndexOf("</");

    if (pos !== -1) {
        xml =
            xml.substring(0, pos) +
            "<card>123321123</card><personId>asd123dsa</personId>" +
            xml.substring(pos);
    }

    r.headersOut["Content-Type"] = "application/xml";
    r.return(200, xml);
}

export default { reply };
