import "./DetailsGrid.css"

function DetailsGrid(){
    return(
        <div className="grid-container">
            <div className="grid-row">
                <div className="grid-element element1">Ex1</div>
                <div className="grid-element element2">Ex2</div>
            </div>
            <div className="grid-row">
                <div className="grid-element element3">Ex3</div>
                <div className="grid-element element4">Ex4</div>
            </div>
        </div>
    );

}

export default DetailsGrid;