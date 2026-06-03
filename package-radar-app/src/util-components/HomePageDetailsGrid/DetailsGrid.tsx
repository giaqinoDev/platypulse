import "./DetailsGrid.css"

function DetailsGrid(){
    return(
        <div className="grid-container">
            <div className="grid-row">
                <div className="grid-element element1">
                    <h3 className="grid-element-header">Comprehensive Package Analysis</h3>
                    <p className="grid-element-p">Evaluate packages using maintenance, releases, dependencies, documentation, and community activity all in one place.</p>
                </div>
                <div className="grid-element element2">
                    <h3 className="grid-element-header">Pulse Check Your Dependencies</h3>
                    <p className="grid-element-p">See the heartbeat of any package with a health score built from real ecosystem signals and activity metrics.</p>
                </div>
            </div>
            <div className="grid-row">
                <div className="grid-element element3">
                    <h3 className="grid-element-header">Ship With Confidence</h3>
                    <p className="grid-element-p">Reduce dependency risk by identifying well-maintained libraries before they become part of your stack.</p>
                </div>
                <div className="grid-element element4">Ex4</div>
            </div>
        </div>
    );

}

export default DetailsGrid;