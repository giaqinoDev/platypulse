import "../sub-score/SubScoreChart.css";
interface SubScoreChartProps{
    category: string;
    score: number | undefined;
}
function SubScoreChart({category, score}: SubScoreChartProps){
    const floored_score = score !== undefined ? Math.floor(score): 0;
    return(
        floored_score !== undefined &&
            <div >
                <div className="sub-score-category-container">
                    <p>{category}</p>
                    <p className="sub-score">{floored_score}</p>
                </div>
                
                    <div id="score-bar-container">
                        <div id="bar-one" className={`sub-score-bar ${floored_score >= 25 && "active"}`}>

                        </div>

                        <div id="bar-two" className={`sub-score-bar ${floored_score >= 50 && "active"}`}>

                        </div>

                        <div id="bar-three" className={`sub-score-bar ${floored_score >=75  && "active"}`}>

                        </div>

                        <div id="bar-four" className={`sub-score-bar ${floored_score <= 100 && floored_score >=90 && "active"}`}>

                        </div>
                    </div>
            </div>
    );
}

export default SubScoreChart;
