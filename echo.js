function reply(r) {
    r.return(200, r.requestText);
}

export default { reply };
