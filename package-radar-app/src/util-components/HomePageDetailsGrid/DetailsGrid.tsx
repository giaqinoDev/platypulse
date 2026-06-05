import "./DetailsGrid.css"
import { FaHeartPulse } from "react-icons/fa6";
import platypusMagnifying from "../../assets/HomeSplashScreenGraphic/PlatypusMagnifyingGlass.svg"
import { TbStack2Filled } from "react-icons/tb";
import platypusOnGraph from "../../assets/HomeSplashScreenGraphic/PlatypusOnGraph.svg"

function DetailsGrid(){
    return(
        <div className="grid-container">
            <div className="grid-row">
                <div className="grid-element element1">
                    <h3 className="grid-element-header">Comprehensive Package Analysis</h3>
                    <p className="grid-element-p" style={{"marginBottom": "0px"}}>Evaluate packages using maintenance, releases, dependencies, documentation, and community activity all in one place.</p>
                    <div style={{"display": "flex", "height": "fitContent"}}>
                        <img src={platypusMagnifying} className="platypus-magnifying-svg"></img>
                    </div>
                </div>
                <div className="grid-element element2">
                    <FaHeartPulse className="heart-pulse-icon"/>
                    <h3 className="grid-element-header" style={{"textAlign": "center"}}>Pulse Check Your Dependencies</h3>
                    <p className="grid-element-p" style={{"textAlign": "center"}}>See the heartbeat of any package with a health score built from real ecosystem signals and activity metrics.</p>
                </div>
            </div>
            <div className="grid-row">
                <div className="grid-element element3">
                    <div style={{"display": "flex", "alignItems": "center"}}>
                        <h3 className="grid-element-header">Ship With Confidence</h3>
                        <TbStack2Filled className="stack-icon"/>
                    </div>
                    <p className="grid-element-p">Reduce dependency risk by identifying well-maintained libraries before they become part of your stack.</p>
                </div>
                <div className="grid-element element4">
                    <img src={platypusOnGraph} className="platypus-graph-svg"></img>
                    <div>
                        <h3 className="grid-element-header">Visualize Signals</h3>
                        <p className="grid-element-p">Visualize signals across the board, from community adoption to development timelines.</p>
                    </div>
                </div>
            </div>
        </div>
    );

}

export default DetailsGrid;