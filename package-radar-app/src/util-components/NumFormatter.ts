function formatNumber(value: number|undefined) {
    if(typeof value == 'undefined'){
        return undefined;
    }
    return new Intl.NumberFormat("en-US", {
        notation: "compact",
        maximumFractionDigits: 1,
    }).format(value);
}

export default formatNumber;