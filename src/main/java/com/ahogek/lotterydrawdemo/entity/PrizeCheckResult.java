package com.ahogek.lotterydrawdemo.entity;

/**
 * @author AhogeK
 * @since 2024-11-27 13:00:32
 */
public class PrizeCheckResult {

    private Integer firstPrize;

    private Integer secondPrize;

    private Integer thirdPrize;

    private Integer fourthPrize;

    private Integer fifthPrize;

    private Integer sixthPrize;

    private Integer seventhPrize;

    private Integer eighthPrize;

    private Integer ninthPrize;

    public PrizeCheckResult() {
    }

    public PrizeCheckResult(Integer firstPrize, Integer secondPrize, Integer thirdPrize, Integer fourthPrize, Integer fifthPrize,
                            Integer sixthPrize, Integer seventhPrize, Integer eighthPrize, Integer ninthPrize) {
        this.firstPrize = firstPrize;
        this.secondPrize = secondPrize;
        this.thirdPrize = thirdPrize;
        this.fourthPrize = fourthPrize;
        this.fifthPrize = fifthPrize;
        this.sixthPrize = sixthPrize;
        this.seventhPrize = seventhPrize;
        this.eighthPrize = eighthPrize;
        this.ninthPrize = ninthPrize;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Integer getFirstPrize() {
        return firstPrize;
    }

    public void setFirstPrize(Integer firstPrize) {
        this.firstPrize = firstPrize;
    }

    public Integer getSecondPrize() {
        return secondPrize;
    }

    public void setSecondPrize(Integer secondPrize) {
        this.secondPrize = secondPrize;
    }

    public Integer getThirdPrize() {
        return thirdPrize;
    }

    public void setThirdPrize(Integer thirdPrize) {
        this.thirdPrize = thirdPrize;
    }

    public Integer getFourthPrize() {
        return fourthPrize;
    }

    public void setFourthPrize(Integer fourthPrize) {
        this.fourthPrize = fourthPrize;
    }

    public Integer getFifthPrize() {
        return fifthPrize;
    }

    public void setFifthPrize(Integer fifthPrize) {
        this.fifthPrize = fifthPrize;
    }

    public Integer getSixthPrize() {
        return sixthPrize;
    }

    public void setSixthPrize(Integer sixthPrize) {
        this.sixthPrize = sixthPrize;
    }

    public Integer getSeventhPrize() {
        return seventhPrize;
    }

    public void setSeventhPrize(Integer seventhPrize) {
        this.seventhPrize = seventhPrize;
    }

    public Integer getEighthPrize() {
        return eighthPrize;
    }

    public void setEighthPrize(Integer eighthPrize) {
        this.eighthPrize = eighthPrize;
    }

    public Integer getNinthPrize() {
        return ninthPrize;
    }

    public void setNinthPrize(Integer ninthPrize) {
        this.ninthPrize = ninthPrize;
    }

    @Override
    public String toString() {
        return String.format("""
                        历史中奖统计:
                                    一等奖: %d次 (奖金: 浮动)
                                    二等奖: %d次 (奖金: 浮动)
                                    三等奖: %d次 (奖金: 10,000元)
                                    四等奖: %d次 (奖金: 3,000元)
                                    五等奖: %d次 (奖金: 300元)
                                    六等奖: %d次 (奖金: 200元)
                                    七等奖: %d次 (奖金: 100元)
                                    八等奖: %d次 (奖金: 15元)
                                    九等奖: %d次 (奖金: 5元)
                        """,
                firstPrize, secondPrize, thirdPrize, fourthPrize, fifthPrize, sixthPrize, seventhPrize, eighthPrize, ninthPrize
        );
    }

    public static class Builder {
        private Integer firstPrize;

        private Integer secondPrize;

        private Integer thirdPrize;

        private Integer fourthPrize;

        private Integer fifthPrize;

        private Integer sixthPrize;

        private Integer seventhPrize;

        private Integer eighthPrize;

        private Integer ninthPrize;

        public Builder firstPrize(Integer firstPrize) {
            this.firstPrize = firstPrize == null ? 0 : firstPrize;
            return this;
        }

        public Builder secondPrize(Integer secondPrize) {
            this.secondPrize = secondPrize == null ? 0 : secondPrize;
            return this;
        }

        public Builder thirdPrize(Integer thirdPrize) {
            this.thirdPrize = thirdPrize == null ? 0 : thirdPrize;
            return this;
        }

        public Builder fourthPrize(Integer fourthPrize) {
            this.fourthPrize = fourthPrize == null ? 0 : fourthPrize;
            return this;
        }

        public Builder fifthPrize(Integer fifthPrize) {
            this.fifthPrize = fifthPrize == null ? 0 : fifthPrize;
            return this;
        }

        public Builder sixthPrize(Integer sixthPrize) {
            this.sixthPrize = sixthPrize == null ? 0 : sixthPrize;
            return this;
        }

        public Builder seventhPrize(Integer seventhPrize) {
            this.seventhPrize = seventhPrize == null ? 0 : seventhPrize;
            return this;
        }

        public Builder eighthPrize(Integer eighthPrize) {
            this.eighthPrize = eighthPrize == null ? 0 : eighthPrize;
            return this;
        }

        public Builder ninthPrize(Integer ninthPrize) {
            this.ninthPrize = ninthPrize == null ? 0 : ninthPrize;
            return this;
        }

        public PrizeCheckResult build() {
            return new PrizeCheckResult(firstPrize, secondPrize, thirdPrize,
                    fourthPrize, fifthPrize, sixthPrize,
                    seventhPrize, eighthPrize, ninthPrize);
        }
    }
}
